name := """wacc-backend"""
organization := "rug.wacc"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

//libraryDependencies += "org.joda" % "joda-convert" % "1.8"
libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "4.11"

//libraryDependencies += "com.netaporter" % "scala-uri_2.12" % "0.4.16"
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.1.0"

// ReactiveMongo, see http://reactivemongo.org/releases/0.12/documentation/tutorial/play.html
libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.6-play26"
)

// sbt-native-packager for Docker
// see http://www.scala-sbt.org/sbt-native-packager/formats/docker.html
enablePlugins(DockerPlugin)
version in Docker := "latest"
// dockerUsername in Docker := Option("wacccourse")
// dockerAlias in Docker := new com.typesafe.sbt.packager.docker.DockerAlias(Option("index.docker.io"),Option("wacccourse"), "backend", Option("latest"))

// scalaz -> we use EitherT
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.15"

// authorization -> http://deadbolt.ws/
libraryDependencies ++= Seq(
  "be.objectify" %% "deadbolt-scala" % "2.6.0"
)

// JWT support
libraryDependencies += "io.igl" %% "jwt" % "1.2.2"

// Mockito (test mocks)
libraryDependencies += "org.mockito" % "mockito-core" % "2.10.0" % "test"

// connector of cassandra
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.3.0"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-mapping" % "3.3.0"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-extras" % "3.3.0"
libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.2"
)

resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "com.outworkers"  %% "phantom-dsl" % "2.14.5"
)
// libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" %


libraryDependencies ++= Seq(
  ws
)
