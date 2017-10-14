package models
import java.util.UUID

import com.outworkers.phantom.dsl._

import scala.concurrent.Future

/**
  * Create the Cassandra representation of the Songs table
  */
abstract class ConsulEventModel extends Table[ConsulEventModel, ConsulEvent] {

  override def tableName: String = "consul_events"

  object id extends UUIDColumn with PartitionKey {
    override lazy val name = "consul_event_id"
  }

  object events extends JsonColumn[ConsulEventItem]

  object datetime extends DateTimeColumn with PrimaryKey

  override def fromRow(row: Row): ConsulEvent = {
    ConsulEvent(
      id(row),
      events(row),
      datetime(row)
    )
  }

  def getByConsulEventId(id: UUID): Future[Option[ConsulEvent]] = {
    select
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()
  }

  def deleteById(id: UUID): Future[ResultSet] = {
    delete
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }
}