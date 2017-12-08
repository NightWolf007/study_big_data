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
 * Calculates count of twitter posts per minute
 */
object Main {
  val defaultCassandraHost = "cassandra"
  val defaultCassandraPort = 9042
  val defaultSparkMaster = "local[2]"

  /**
   * Main function
   * Args: [cassandraHost, cassandraPort, sparkMaster]
   * cassandraHost - host of cassandra server (default: 0.0.0.0)
   * cassandraPort - port of cassandra server (default: 9042)
   * sparkMaster - host of spark master server (default: local[2])
   */
  def main(args: Array[String]) {
    args.length match {
      case 0 => run()
      case 1 => run(args(0))
      case 2 => run(args(0), args(1).toInt)
      case _ => run(args(0), args(1).toInt, args(2))
    }
  }

  /**
   * Run function starts application
   * cassandraHost - host of cassandra server (default: 0.0.0.0)
   * cassandraPort - port of cassandra server (default: 9042)
   * sparkMaster - host of spark master server (default: local[2])
   */
  def run(cassandraHost: String = defaultCassandraHost, cassandraPort: Int = defaultCassandraPort,
          sparkMaster: String = defaultSparkMaster) {
    val cassandraSession = Cluster
      .builder
      .addContactPointsWithPorts(new java.net.InetSocketAddress(cassandraHost, cassandraPort))
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
                                   .setMaster(sparkMaster)
                                   .set("spark.shuffle.service.enabled", "true")
                                   .set("spark.dynamicAllocation.enabled", "true")
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val eventCountsStream = calculate(ssc, events)

    val eventCountsBuffer = new ListBuffer[(Long, Int)]()
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

  /**
   * Calculate function calculates events count per minute
   * ssc - Spark Streaming context
   * events - List of timestamps
   * Returns Spark stream
   */
  def calculate(ssc: StreamingContext, events: List[Long]):DStream[(Long, Int)] = {
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
    return stream.map(x => (x(0), x.length))
  }
}
