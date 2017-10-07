name := """wacc-backend"""
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

// ReactiveMongo, see http://reactivemongo.org/releases/0.12/documentation/tutorial/play.html
libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.6-play26"
)

// sbt-native-packager for Docker
// see http://www.scala-sbt.org/sbt-native-packager/formats/docker.html
enablePlugins(DockerPlugin)
version in Docker := "latest"


// scalaz -> we use EitherT
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.15"

// authorization -> http://deadbolt.ws/
libraryDependencies ++= Seq(
  "be.objectify" %% "deadbolt-scala" % "2.6.0"
)
