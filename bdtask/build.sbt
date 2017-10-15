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
      sparkCore
    )
  )
