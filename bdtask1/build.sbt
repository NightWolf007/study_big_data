name := "bdtask1"
version := "1.0"

scalaVersion := "2.12.3"
scalacOptions := Seq("-unchecked", "-deprecation", "-feature")

mainClass in Compile := Some("Main")
scalaSource in Compile := baseDirectory.value / "src"
scalaSource in Test := baseDirectory.value / "test"

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.6.0" % "provided"
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.6.0" % "test"
libraryDependencies += "org.apache.mrunit" % "mrunit" % "1.1.0" % "test" classifier "hadoop2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

