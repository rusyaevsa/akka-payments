name := "AkkaPay"

version := "0.1"

scalaVersion := "2.12.14"

val AkkaVersion = "2.6.15"

libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-file" % "3.0.2",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion
)
