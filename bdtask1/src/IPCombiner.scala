import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce._
import org.apache.log4j.Logger

/**
 * Provides Combiner implementation.
 * Sums all integers to calculate bytes count and requests count.
 */
class IPCombiner extends IPCombiner.Base {
  import IPCombiner._

  val logger = Logger.getLogger(classOf[IPCombiner])

  override def reduce(key: Text, values: java.lang.Iterable[CompositeWritable], context: Base#Context): Unit = {
    import scala.collection.JavaConverters._

    var requests_count = 0
    var bytes_count = 0
    values.forEach { value =>
      requests_count += value.getFirst
      bytes_count += value.getSecond
    }

    logger.info("IP: " + key + " - " + requests_count + " - " + bytes_count)

    composite.set(requests_count, bytes_count)
    context.write(key, composite)
  }
}

/**
 * Static fields and type definitions for IPCombiner class.
 */
object IPCombiner {
  type Base = Reducer[Text, CompositeWritable, Text, CompositeWritable]

  /**
   * Reusable Writeable for composite values.
   */
  val composite = new CompositeWritable
}
