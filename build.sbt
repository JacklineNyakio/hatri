val sharedSettings = Seq(
  organization := "com.hatri",
  version := "0.1.0",
  scalaVersion := "2.11.12",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:implicitConversions"
  ),
  resolvers ++= Seq(
    "twttr" at "http://maven.twttr.com/",
    Resolver.sonatypeRepo("releases")

  )
)

val kafkaVersion    = "2.3.0"
val twitterVersion  = "6.34.0"
val hbcCoreVersion  = "2.2.0"

val sharedDependecies = libraryDependencies ++= Seq(
  "io.spray"                      %%    "spray-json"              % "1.3.5",
  "com.typesafe.scala-logging"    %%    "scala-logging"           % "3.9.0",
  "org.slf4j"                     %     "slf4j-simple"            % "1.7.25",
  "com.fasterxml.jackson.module"  % "jackson-module-scala"        % "2.0.2",
)
lazy val root = (project in file(".")).aggregate(core, ingestion, compute)

lazy val core = (project in file("core")).settings(
  sharedSettings,
  sharedDependecies,
  libraryDependencies ++= Seq(
    "com.typesafe"                  %    "config"                   % "1.3.0"
  )
)

lazy val ingestion = (project in file("ingestion")).settings(
  name := "data-ingestion-point",
  sharedSettings,
  sharedDependecies,
  libraryDependencies ++= Seq(
    "org.apache.kafka"            % "kafka-streams"       % "2.3.0",
    "org.apache.kafka"            % "kafka-clients"       % "2.3.0",
    "com.twitter"                 % "hbc-core"                % "2.0.2",
    "com.twitter"                 % "hbc-twitter4j"           % "2.0.2"
  ) 
).dependsOn(core)

lazy val compute = (project in file("compute")).settings(
  name := "data-transformation",
  sharedSettings,
  libraryDependencies ++= Seq(

  )
).dependsOn(core,ingestion)