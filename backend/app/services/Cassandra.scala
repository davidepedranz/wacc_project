package services

import javax.inject.{Inject, Singleton}

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.policies._
import com.outworkers.phantom.connectors.{CassandraConnection, KeySpaceBuilder, KeySpaceCQLQuery}
import play.api.{Configuration, Logger}

/**
  * Helper to connect to Cassandra.
  */
@Singleton
final class Cassandra @Inject()(configuration: Configuration) {

  // read the configuration for Cassandra
  private val cassandraHosts: Seq[String] = configuration.get[String]("cassandra.hosts").split(",")
  private val cassandraPort: Int = configuration.get[Int]("cassandra.port")
  private val cassandraKeyspace: String = configuration.get[String]("cassandra.keyspace")
  private val cassandraReplication: Int = configuration.get[Int]("cassandra.replication")

  // connector for Cassandra
  lazy val connector: CassandraConnection = keyspace().withClusterBuilder(_
    .withReconnectionPolicy(new ConstantReconnectionPolicy(1000)) // retry to reconnect after 1s
    .withRetryPolicy(new LoggingRetryPolicy(DefaultRetryPolicy.INSTANCE)) // make sure to log the failed attempts (for debug purposes)
    .withSpeculativeExecutionPolicy(new ConstantSpeculativeExecutionPolicy(1000, 20)) // retry after 1s, max 20 times
  )
    .keySpace(new KeySpaceCQLQuery {
      override def keyspace: String = cassandraKeyspace

      override def queryString: String =
        s"CREATE KEYSPACE IF NOT EXISTS $cassandraKeyspace WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': '$cassandraReplication'}  AND DURABLE_WRITES = true;"
    })

  // manually initialize the keyspace (so we can handle errors)
  // Phantom hides the creation of the cluster and masquerade some exceptions
  // https://stackoverflow.com/questions/39727744/datastax-java-driver-does-not-connect-if-one-host-is-missing
  private def keyspace() = {
    try {
      val builder: Cluster.Builder = new Cluster.Builder
      cassandraHosts.foreach(point => builder.addContactPoint(point))
      builder.withPort(cassandraPort)
      new KeySpaceBuilder(_ => builder)
    } catch {
      case t: Throwable =>
        Logger.error("Problem during application startup: Cassandra hosts cannot be resolved.", t)
        Logger.error("Crashing the application. Hopefully, some orchestrator will restart me.")
        System.exit(1)
        null
    }
  }
}