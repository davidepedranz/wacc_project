name := """dashboard-service"""
organization := "rug.wacc"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

libraryDependencies += "org.joda" % "joda-convert" % "1.8"
libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "4.11"

libraryDependencies += "com.netaporter" % "scala-uri_2.12" % "0.4.16"
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.1.0"


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "rug.wacc.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "rug.wacc.binders._"
