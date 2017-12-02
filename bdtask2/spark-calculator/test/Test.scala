import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scala.collection.mutable.{Queue, ListBuffer}
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Span}
import org.scalatest.FlatSpec

class Test extends FlatSpec with Eventually {
  "Main" should "should calculate through spark" in {
    val conf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("SparkCalculator")
    val ssc = new StreamingContext(conf, Seconds(1))

    val events = List(
      1230000L,
      4560000L,
      1230000L,
      7890000L,
      7890000L,
      1230000L
    )

    val eventCountsStream = Main.calculate(ssc, events)

    val eventCountsBuffer = new ListBuffer[Int]()
    eventCountsStream.foreachRDD {
      eventCountsBuffer ++= _.collect
    }
    eventCountsStream.print()

    ssc.start()
    ssc.awaitTerminationOrTimeout(10000) 

    eventually {
      val eventCounts = eventCountsBuffer.toList
      assert(eventCounts.contains(3))
      assert(eventCounts.contains(2))
      assert(eventCounts.contains(1))
    }
  }
}
