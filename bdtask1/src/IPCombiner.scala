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

  override def reduce(key: Text, values: java.lang.Iterable[CompositeWriteable], context: Base#Context): Unit = {
    import scala.collection.JavaConverters._

    val composite_values = values.asScala.toList
    val requests_count = composite_values.map(_.getFirst).sum
    val bytes_count = composite_values.map(_.getSecond).sum

    logger.info("IP: " + key + " - " + requests_count + " - " + bytes_count)

    composite.set(requests_count, bytes_count)
    context.write(key, composite)
  }
}

/**
 * Static fields and type definitions for IPCombiner class.
 */
object IPCombiner {
  type Base = Reducer[Text, CompositeWriteable, Text, CompositeWriteable]

  /**
   * Reusable Writeable for composite values.
   */
  val composite = new CompositeWriteable
}
