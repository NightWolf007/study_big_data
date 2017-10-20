package main.scala

import java.io.IOException
import java.io.DataInput
import java.io.DataOutput
import java.util._
import org.apache.hadoop.fs.Path
import org.apache.hadoop.conf._
import org.apache.hadoop.io._
import org.apache.hadoop.mapred._
import org.apache.hadoop.util._
import org.apache.log4j.Logger;
 
object BDTask {
  class CompositeWriteable(var val1: Int, var val2: Int) extends Writable {
    def this() = this(0, 0)

    def set(val1: Int, val2: Int) {
      this.val1 = val1
      this.val2 = val2
    }

    def getFirst(): Int = val1
    def getSecond(): Int = val2

    @throws[IOException]
    override def readFields(in: DataInput) {
      val1 = in.readInt();
      val2 = in.readInt();
    }

    @throws[IOException]
    override def write(out: DataOutput) {
      out.writeInt(val1);
      out.writeInt(val2);
    }

    override def toString(): String = val1.toString + ":" + val2.toString
  }
 
  class BDTaskMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, CompositeWriteable] {
    private val logger = Logger.getLogger(classOf[Map]);
    private val ip = new Text()
 
    @throws[IOException]
    def map(key: LongWritable, value: Text, output: OutputCollector[Text, CompositeWriteable], reporter: Reporter) {
      line: String = value.toString
      logger.info(line)
      val tokens = line.split(" ")
      if (tokens.length >= 10) {
        ip.set(tokens(0))
        val bytes_count = tokens(9).toInt
        logger.info("IP: " + ip + " - " + bytes_count)
        output.collect(ip, new CompositeWriteable(1, bytes_count))
      }
    }
  }
 
  class BDTaskReducer extends MapReduceBase with Reducer[Text, CompositeWriteable, Text, CompositeWriteable] {
    private val logger = Logger.getLogger(classOf[Reduce]);

    @throws[IOException]
    def reduce(key: Text, values: Iterator[CompositeWriteable], output: OutputCollector[Text, CompositeWriteable], reporter: Reporter) {
      import scala.collection.JavaConversions._
      val sum = values.toList.reduce({ (valueOne, valueTwo) =>
        logger.info("IP: " + key.toString + " - " + valueOne.toString + " - " + valueTwo.toString)
        val count = valueOne.getFirst + valueTwo.getFirst
        val bytes = valueOne.getSecond + valueTwo.getSecond
        new CompositeWriteable(count, bytes)
      })
      output.collect(key, sum)
    }
  }
 
  @throws[Exception]
  def main(args: Array[String]) {
    val conf: JobConf = new JobConf(this.getClass)
    conf.setJobName("BDTask")
    // conf.setInputFormat(classOf[TextInputFormat])
    // conf.setOutputFormat(classOf[TextOutputFormat[Text, CompositeWriteable]])
    conf.setMapperClass(classOf[BDTaskMapper])
    conf.setCombinerClass(classOf[BDTaskReducer])
    conf.setReducerClass(classOf[BDTaskReducer])
    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[CompositeWriteable])
    FileInputFormat.setInputPaths(conf, new Path(args(0)))
    FileOutputFormat.setOutputPath(conf, new Path(args(1)))
    JobClient.runJob(conf)
  }
}
