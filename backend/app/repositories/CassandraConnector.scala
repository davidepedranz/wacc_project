package repositories

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints}
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

object CassandraConnector {
  private val config = ConfigFactory.load()

  private val hosts = config.getStringList("cassandra.host").asScala
  private val keyspace = config.getString("cassandra.keyspace")

  // TODO: read the port for cassandra from the configuration
  /**
    * Create a connector with the ability to connects to multiple hosts in a cluster.
    */
  lazy val connector: CassandraConnection = ContactPoints(hosts).keySpace(keyspace)
}