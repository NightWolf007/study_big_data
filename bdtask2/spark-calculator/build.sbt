name := "spark-calculator"
version := "1.0"

scalaVersion := "2.11.3"
scalacOptions := Seq("-unchecked", "-deprecation", "-feature")

mainClass in Compile := Some("Main")
scalaSource in Compile := baseDirectory.value / "src"
scalaSource in Test := baseDirectory.value / "test"

libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.2.0"
// libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.2.0" % "provided"
libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.2.0" % "provided"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _ @ _*) => MergeStrategy.discard
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}
