import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce._
import org.apache.log4j.Logger

/**
 * Provides Reducer implementation.
 * Sums all integers to calculate bytes count, requests count.
 * Also calculates average bytes per request.
 */
class IPReducer extends IPReducer.Base {
  import IPReducer._

  val logger = Logger.getLogger(classOf[IPReducer])

  override def reduce(key: Text, values: java.lang.Iterable[CompositeWritable], context: Base#Context): Unit = {
    import scala.collection.JavaConverters._

    var requests_count = 0
    var bytes_count = 0
    values.forEach { value =>
      requests_count += value.getFirst
      bytes_count += value.getSecond
    }
    val average: Double = bytes_count / requests_count

    logger.info("IP: " + key + " - " + average + " - " + bytes_count + " - ")

    text.set(average + "," + bytes_count)
    context.write(key, text)
  }
}

/**
 * Static fields and type definitions for IPReducer class.
 */
object IPReducer {
  type Base = Reducer[Text, CompositeWritable, Text, Text]

  /**
   * Reusable Writeable for text.
   */
  val text = new Text
}
