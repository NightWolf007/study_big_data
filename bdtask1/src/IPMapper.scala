import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce._
import org.apache.log4j.Logger

import scala.annotation.tailrec

/**
 * Provides Mapper implementation.
 * Extracts IP and bytes count from log line.
 */
class IPMapper extends IPMapper.Base {
  import IPMapper._

  val logger = Logger.getLogger(classOf[IPMapper])

  override def map(key: Any, value: Text, context: Base#Context): Unit = {
    /* 
     * 1. Filter invalid strings.
     * 2. Extract IP and bytes count.
     */

    logger.info(value.toString)

    regexp
      .findFirstMatchIn(value.toString)
      .map(_.subgroups)
      .foreach { subgroup =>
        val ip = subgroup(0).toString
        val bytes_count = subgroup(1).toInt

        logger.info("IP: " + ip + " - " + bytes_count)

        text.set(ip)
        composite.set(1, bytes_count)
        context.write(text, composite)
      }
  }
}

/**
 * Static fields and type definitions for IPMapper class.
 */
object IPMapper {
  type Base = Mapper[Any, Text, Text, CompositeWritable]

  /**
   * Reusable Writable for texts.
   */
  val text = new Text
  /**
   * Reusable Writeable for composite values.
   */
  val composite = new CompositeWritable

  /**
   * Log line format.
   */
  val regexp = "^(\\d+\\.\\d+\\.\\d+\\.\\d+) - - \\[.*?\\] \".*?\" \\d+ (\\d+) \".*?\" \".*?\"$".r
}
