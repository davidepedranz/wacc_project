package services

import javax.inject.{Inject, Singleton}

import com.datastax.driver.core.policies._
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints, KeySpaceCQLQuery}
import play.api.Configuration

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
  lazy val connector: CassandraConnection = ContactPoints(cassandraHosts, cassandraPort)
    .withClusterBuilder(_
      .withReconnectionPolicy(new ConstantReconnectionPolicy(1000)) // retry to reconnect after 1s
      .withRetryPolicy(new LoggingRetryPolicy(DefaultRetryPolicy.INSTANCE)) // make sure to log the failed attempts (for debug purposes)
      .withSpeculativeExecutionPolicy(new ConstantSpeculativeExecutionPolicy(1000, 20)) // retry after 1s, max 20 times
    )
    .keySpace(new KeySpaceCQLQuery {
      override def keyspace: String = cassandraKeyspace

      override def queryString: String =
        s"CREATE KEYSPACE IF NOT EXISTS $cassandraKeyspace WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': '$cassandraReplication'}  AND DURABLE_WRITES = true;"
    })
}