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
      "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.7.3",
      "org.apache.hadoop" % "hadoop-common" % "2.7.3"
    ),

    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
      case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
      case PathList("org", "apache", xs @ _*) => MergeStrategy.last
      case PathList("com", "google", xs @ _*) => MergeStrategy.last
      case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
      case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
      case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
      case "about.html" => MergeStrategy.rename
      case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
      case "META-INF/mailcap" => MergeStrategy.last
      case "META-INF/mimetypes.default" => MergeStrategy.last
      case "plugin.properties" => MergeStrategy.last
      case "log4j.properties" => MergeStrategy.last
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },

    scalacOptions += "-target:jvm-1.8",
    javacOptions in (Compile, compile) ++= Seq("-source", "1.8", "-target", "1.8", "-g:lines"),
    mainClass in (Compile,run) := Some("WordCount"),
    crossPaths := false,
    autoScalaLibrary := false
  )
