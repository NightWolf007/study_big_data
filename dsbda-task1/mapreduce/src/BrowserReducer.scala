import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce._

/**
 * Provides Reducer implementation.
 * Sums all integers to calculate browsers count.
 */
class BrowserReducer extends BrowserReducer.Base {
  import BrowserReducer._

  override def reduce(key: Text, values: java.lang.Iterable[IntWritable], context: Base#Context): Unit = {
    import scala.collection.JavaConverters._

    /* Sum all integers in iterable. */
    int.set(values.asScala.map(_.get).sum)
    context.write(key, int)
  }
}

/**
 * Static fields and type definitions for BrowserReducer class.
 */
object BrowserReducer {
  type Base = Reducer[Text, IntWritable, Text, IntWritable]

  /**
   * Reusable Writable for single integer values.
   */
  val int = new IntWritable(1)
}
