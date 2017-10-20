import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce._

import scala.annotation.tailrec

/**
 * Provides Mapper implementation.
 * Extracts browser name from log line.
 */
class BrowserMapper extends BrowserMapper.Base {
  import BrowserMapper._

  /**
   * Checks whether string contains all strings from list.
   *
   * @param s String to check.
   * @param values The list of strings to search for.
   */
  @tailrec private def contains(s: String, values: List[String]): Boolean = values match {
    case head :: tail => if (s.contains(head)) contains(s, tail) else false
    case Nil => true
  }

  private def contains(s: String, values: String*): Boolean = contains(s, values.toList)

  override def map(key: Any, value: Text, context: Base#Context): Unit = {
    /* 
     * 1. Filter invalid strings.
     * 2. Extract user agent.
     * 3. Extract browser info from user agent.
     */

    regexp
      .findFirstMatchIn(value.toString)
      .flatMap(_.subgroups.lift(0))
      .map {
        case s if contains(s, "Gecko/", "Firefox/") => "Firefox"
        case s if contains(s, "Gecko/", "Thunderbird/") => "Thunderbird"
        case s if contains(s, "AppleWebKit/", "Edge/") => "Edge"
        case s if contains(s, "AppleWebKit/", "Chrome/") => "Chromium"
        case s if contains(s, "AppleWebKit/", "Safari/") => "Safari"
        case _ => "Other"
      }
      .foreach { browser =>
        text.set(browser)
        context.write(text, one)
      }
  }
}

/**
 * Static fields and type definitions for BrowserMapper class.
 */
object BrowserMapper {
  type Base = Mapper[Any, Text, Text, IntWritable]

  /**
   * Reusable Writable for single integer values.
   */
  val one = new IntWritable(1)

  /**
   * Reusable Writable for texts.
   */
  val text = new Text

  /**
   * Log line format.
   */
  val regexp = "^\\d+\\.\\d+\\.\\d+\\.\\d+ - - \\[.*?\\] \".*?\" \\d+ \\d+ \".*?\" \"(.*?)\"$".r
}
