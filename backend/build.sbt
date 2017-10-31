name := """wacc-backend"""
organization := "rug.wacc"
version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
scalaVersion := "2.12.3"

// sbt-native-packager for Docker
// see http://www.scala-sbt.org/sbt-native-packager/formats/docker.html
enablePlugins(DockerPlugin)
version in Docker := "latest"

// dependencies
libraryDependencies ++= Seq(

  // utilities
  ws,
  guice,
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "net.logstash.logback" % "logstash-logback-encoder" % "4.11",
  "org.scalaz" %% "scalaz-core" % "7.2.15",

  // JWT
  "io.igl" %% "jwt" % "1.2.2",

  // authorizations
  "be.objectify" %% "deadbolt-scala" % "2.6.0",

  // mongodb
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.6-play26",

  // cassandra
  "com.outworkers" %% "phantom-dsl" % "2.14.5",
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.3.0",
  "com.datastax.cassandra" % "cassandra-driver-mapping" % "3.3.0",
  "com.datastax.cassandra" % "cassandra-driver-extras" % "3.3.0",

  // kakfa
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.17",

  // test
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.mockito" % "mockito-core" % "2.10.0" % Test
)

resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)



