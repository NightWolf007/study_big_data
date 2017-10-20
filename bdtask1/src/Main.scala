import org.apache.hadoop._
import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce.lib.input._
import org.apache.hadoop.mapreduce.lib.output._
import org.apache.hadoop.util.GenericOptionsParser

/**
 * Main application object.
 */
object Main extends App {
  val name = "IP counter"

  val configuration = new conf.Configuration
  val optionParser = new GenericOptionsParser(configuration, args)
  val workArgs = optionParser.getRemainingArgs

  /* Create and configure the job. */
  val job = mapreduce.Job.getInstance(configuration, name)

  job.setJarByClass(classOf[IPMapper])
  job.setMapperClass(classOf[IPMapper])
  job.setCombinerClass(classOf[IPCombiner])
  job.setReducerClass(classOf[IPReducer])

  job.setMapOutputKeyClass(classOf[Text])
  job.setMapOutputValueClass(classOf[CompositeWriteable])
  job.setOutputKeyClass(classOf[Text])
  job.setOutputValueClass(classOf[Text])

  job.setInputFormatClass(classOf[TextInputFormat])
  job.setOutputFormatClass(classOf[SequenceFileOutputFormat[Text, Text]])

  FileInputFormat.setInputPaths(job, new fs.Path(workArgs(0)))
  FileOutputFormat.setOutputPath(job, new fs.Path(workArgs(1)))

  sys.exit(if (job.waitForCompletion(true)) 0 else 1)
}
