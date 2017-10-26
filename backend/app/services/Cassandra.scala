package services

import javax.inject.{Inject, Singleton}

import com.datastax.driver.core.policies._
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints}
import play.api.Configuration

@Singleton
final class Cassandra @Inject()(configuration: Configuration) {

  // read the configuration for Cassandra
  private val hosts: Seq[String] = configuration.get[String]("cassandra.hosts").split(",")
  private val port: Int = configuration.get[Int]("cassandra.port")
  private val keyspace: String = configuration.get[String]("cassandra.keyspace")

  // connector for Cassandra
  lazy val connector: CassandraConnection = ContactPoints(hosts, port)
    .withClusterBuilder(_
      .withLoadBalancingPolicy(new RoundRobinPolicy())
      .withReconnectionPolicy(new ConstantReconnectionPolicy(1000)) // retry to reconnect after 1s
      .withRetryPolicy(new LoggingRetryPolicy(DefaultRetryPolicy.INSTANCE))
      .withSpeculativeExecutionPolicy(new ConstantSpeculativeExecutionPolicy(1000, 20)) // retry after 1s, max 20 times
    )
    .keySpace(keyspace)
}