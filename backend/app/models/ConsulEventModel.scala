package models

import com.outworkers.phantom.dsl._

import scala.concurrent.Future

/**
  * Create the Cassandra representation of the Songs table
  */
abstract class ConsulEventModel extends Table[ConsulEventModel, ConsulEvent] {

  override def tableName: String = "consul_events"

  object id extends StringColumn with PartitionKey {
    override lazy val name = "consul_event_id"
  }

  object events extends JsonColumn[ConsulEventItem]

  object datetime extends DateTimeColumn with PrimaryKey

  override def fromRow(row: Row): ConsulEvent = {
    ConsulEvent(
      id(row),
      datetime(row),
      events(row)
    )
  }

  def getByConsulEventId(id: String): Future[List[ConsulEvent]] = {
    select
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
        .fetch()
  }

  def getByConsulEventIdAndTime(id: String, datetime: DateTime): Future[Option[ConsulEvent]] = {
    select
      .where(_.id eqs id).and(_.datetime eqs datetime)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()
  }

  def deleteById(id: String): Future[ResultSet] = {
    delete
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }

  def deleteByConsulEventIdAndTime(id: String, datetime: DateTime): Future[ResultSet] = {
    delete
      .where(_.id eqs id).and(_.datetime eqs datetime)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }
}