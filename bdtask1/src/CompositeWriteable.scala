import java.io.IOException
import java.io.DataInput
import java.io.DataOutput
import org.apache.hadoop.io._

/**
 * Provides Writable implementation.
 * Can store two values.
 */
class CompositeWritable(var val1: Int, var val2: Int) extends Writable {
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

  override def toString(): String = val1.toString + "," + val2.toString

  override def equals(o: Any) = o match {
    case that: CompositeWritable => that.val1.equals(this.val1) && that.val2.equals(this.val2)
    case _ => false
  }
}
