import org.apache.hadoop.io._
import org.apache.hadoop.mrunit.mapreduce._

import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class Test extends FlatSpec {
  val mapper = new BrowserMapper
  val reducer = new BrowserReducer

  def mapDriver = MapDriver.newMapDriver(mapper)
  def reduceDriver = ReduceDriver.newReduceDriver(reducer)

  val testStrings = io.Source.fromFile("testlog.log").getLines.toArray

  "The BrowserMapper" should "recognize Firefox browser" in {
    val suiteMapDriver = mapDriver
    suiteMapDriver.withInput(new Text(""), new Text(testStrings(0)))
    suiteMapDriver.withOutput(new Text("Firefox"), new IntWritable(1))
    suiteMapDriver.runTest()
  }

  "The BrowserMapper" should "recognize Thunderbird browser" in {
    val suiteMapDriver = mapDriver
    suiteMapDriver.withInput(new Text(""), new Text(testStrings(1)))
    suiteMapDriver.withOutput(new Text("Thunderbird"), new IntWritable(1))
    suiteMapDriver.runTest()
  }

  "The BrowserMapper" should "recognize Edge browser" in {
    val suiteMapDriver = mapDriver
    suiteMapDriver.withInput(new Text(""), new Text(testStrings(2)))
    suiteMapDriver.withOutput(new Text("Edge"), new IntWritable(1))
    suiteMapDriver.runTest()
  }

  "The BrowserMapper" should "recognize Chromium browser" in {
    val suiteMapDriver = mapDriver
    suiteMapDriver.withInput(new Text(""), new Text(testStrings(3)))
    suiteMapDriver.withOutput(new Text("Chromium"), new IntWritable(1))
    suiteMapDriver.runTest()
  }

  "The BrowserMapper" should "recognize Safari browser" in {
    val suiteMapDriver = mapDriver
    suiteMapDriver.withInput(new Text(""), new Text(testStrings(4)))
    suiteMapDriver.withOutput(new Text("Safari"), new IntWritable(1))
    suiteMapDriver.runTest()
  }

  "The BrowserMapper" should "recognize Other browser" in {
    val suiteMapDriver = mapDriver
    suiteMapDriver.withInput(new Text(""), new Text(testStrings(5)))
    suiteMapDriver.withOutput(new Text("Other"), new IntWritable(1))
    suiteMapDriver.runTest()
  }

  "The BrowserMapper" should "recognize nothing" in {
    val suiteMapDriver = mapDriver
    suiteMapDriver.withInput(new Text(""), new Text("invalid string"))
    suiteMapDriver.runTest()
  }

  "The BrowserReducer" should "sum all integers" in {
    val suiteReduceDriver = reduceDriver
    suiteReduceDriver.withInput(new Text("Chromium"), List(new IntWritable(2), new IntWritable(4)).asJava)
    suiteReduceDriver.withOutput(new Text("Chromium"), new IntWritable(6))
    suiteReduceDriver.runTest()
  }
}
