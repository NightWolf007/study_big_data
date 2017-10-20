import org.apache.hadoop.io._
import org.apache.hadoop.mrunit.mapreduce._

import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class Test extends FlatSpec {
  val mapper = new IPMapper
  val combiner = new IPCombiner
  val reducer = new IPReducer

  def mapDriver = MapDriver.newMapDriver(mapper)
  def combineDriver = ReduceDriver.newReduceDriver(combiner)
  def reduceDriver = ReduceDriver.newReduceDriver(reducer)

  val testStrings = io.Source.fromFile("testlog.log").getLines.toArray

  "The IPMapper" should "return bytes" in {
    val suiteMapDriver = mapDriver
    suiteMapDriver.withInput(new Text(""), new Text(testStrings(0)))
    suiteMapDriver.withOutput(new Text("178.7.31.65"), new CompositeWritable(1, 10100))
    suiteMapDriver.runTest()
  }

  "The IPMapper" should "ignore invalid lines" in {
    val suiteMapDriver = mapDriver
    suiteMapDriver.withInput(new Text(""), new Text("invalid string"))
    suiteMapDriver.runTest()
  }

  "The IPCombiner" should "return requests count and bytes count" in {
    val suiteCombineDriver = combineDriver
    suiteCombineDriver.withInput(
      new Text("178.7.31.65"),
      List(new CompositeWritable(3, 10100), new CompositeWritable(5, 4000)).asJava
    )
    suiteCombineDriver.withOutput(new Text("178.7.31.65"), new CompositeWritable(8, 14100))
    suiteCombineDriver.runTest()
  }

  "The IPReducer" should "return average bytes and total bytes as text" in {
    val suiteReduceDriver = reduceDriver
    suiteReduceDriver.withInput(
      new Text("178.7.31.65"),
      List(new CompositeWritable(3, 10100), new CompositeWritable(2, 4000)).asJava
    )
    suiteReduceDriver.withOutput(new Text("178.7.31.65"), new Text("2820.0,14100"))
    suiteReduceDriver.runTest()
  }
}
