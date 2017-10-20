import org.apache.hadoop._
import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce.lib.input._
import org.apache.hadoop.mapreduce.lib.output._
import org.apache.hadoop.util.GenericOptionsParser

/**
 * Main application object.
 */
object Main extends App {
  val name = "UA counter"

  val configuration = new conf.Configuration
  val optionParser = new GenericOptionsParser(configuration, args)
  val workArgs = optionParser.getRemainingArgs

  /* Create and configure the job. */
  val job = mapreduce.Job.getInstance(configuration, name)

  job.setJarByClass(classOf[BrowserMapper])
  job.setMapperClass(classOf[BrowserMapper])
  job.setCombinerClass(classOf[BrowserReducer])
  job.setReducerClass(classOf[BrowserReducer])

  job.setOutputKeyClass(classOf[Text])
  job.setOutputValueClass(classOf[IntWritable])

  job.setInputFormatClass(classOf[TextInputFormat])
  job.setOutputFormatClass(classOf[SequenceFileOutputFormat[Text, IntWritable]])

  FileInputFormat.setInputPaths(job, new fs.Path(workArgs(0)))
  FileOutputFormat.setOutputPath(job, new fs.Path(workArgs(1)))

  FileOutputFormat.setCompressOutput(job, true);
  FileOutputFormat.setOutputCompressorClass(job, classOf[compress.SnappyCodec]);
  SequenceFileOutputFormat.setOutputCompressionType(job, SequenceFile.CompressionType.BLOCK);

  sys.exit(if (job.waitForCompletion(true)) 0 else 1)
}
