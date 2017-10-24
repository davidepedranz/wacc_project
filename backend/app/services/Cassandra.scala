package services

import javax.inject.{Inject, Singleton}

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints}
import play.api.Configuration

@Singleton
final class Cassandra @Inject()(configuration: Configuration) {

  // read the configuration for Cassandra
  private val hosts: Seq[String] = configuration.get[Seq[String]]("cassandra.host")
  private val port: Int = configuration.get[Int]("cassandra.port")
  private val keyspace: String = configuration.get[String]("cassandra.keyspace")

  // connector for Cassandra
  lazy val connector: CassandraConnection = ContactPoints(hosts, port).keySpace(keyspace)
}