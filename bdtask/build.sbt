import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.nworg",
      scalaVersion := "2.11.8",
      autoScalaLibrary := true,
      version := "0.1.0-SNAPSHOT"
    )),

    name := "bdtask",

    libraryDependencies ++= Seq(
      scalaTest % Test,
      "org.scala-lang" % "scala-library" % "2.11.8",
      "org.apache.hadoop" % "hadoop-hdfs" % "2.7.3",
      "org.apache.hadoop" % "hadoop-common" % "2.7.3",
      "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.7.3"
    )
  )
