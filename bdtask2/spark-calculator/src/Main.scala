import java.util.Properties
import java.util.Arrays
import java.util.Date
import java.util.UUID
import java.text.SimpleDateFormat

import scala.collection.JavaConverters._
import scala.collection.TraversableOnce
import scala.collection.mutable.{Queue, ListBuffer}

import com.datastax.driver.core._
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.DStream

/**
 * Main application object.
 */
object Main {

  def main(args: Array[String]) { 
    val cassandraSession = Cluster
      .builder
      .addContactPointsWithPorts(new java.net.InetSocketAddress("cassandra", 9042))
      .build
      .connect("BDTASK")

    val result = cassandraSession.execute("select * from event")

    val events = Iterator
      .continually(result.one())
      .takeWhile(_ != null)
      .map(row => {row.get("time", classOf[java.util.Date]).getTime})
      .toList

    cassandraSession.close()

    val sparkConf = new SparkConf().setAppName("SparkCalculator")
                                   .setMaster("local[2]")
                                   .set("spark.shuffle.service.enabled", "true")
                                   .set("spark.dynamicAllocation.enabled", "true")
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val eventCountsStream = calculate(ssc, events)

    val eventCountsBuffer = new ListBuffer[Int]()
    eventCountsStream.foreachRDD {
      eventCountsBuffer ++= _.collect
    }

    ssc.start()
    ssc.awaitTermination()

    val eventCounts = eventCountsBuffer.toList 
    println("===================================================")
    eventCounts.foreach { println(_) }
    println("===================================================")
  }

  def calculate(ssc: StreamingContext, events: List[Long]):DStream[Int] = {
    val period = 5
    println(events)
    println(events.groupBy(e => (e / 1000 / period).toInt).toList)
    val rdds = events.groupBy(e => (e / 1000 / period).toInt)
                     .toList
                     .map{
                       case (key, egrp) =>
                         ssc.sparkContext.parallelize(Seq(egrp))
                     }
    val queue = rdds.foldLeft(new Queue[RDD[List[Long]]])(_ += _)
    val stream = ssc.queueStream(queue)
    return stream.map(_.length)
  }
}
