package models

import com.outworkers.phantom.dsl._
import scala.concurrent.Future

import java.util.Date

/**
  * Create the Cassandra representation of the Songs table
  */
abstract class EventModel extends Table[EventModel, Event] {

  override def tableName: String = "swarm_events"

  object date extends DateColumn with PartitionKey {
    override lazy val name = "event_date"
  }

  object time extends LongColumn with PrimaryKey
  object action extends StringColumn
  object service extends StringColumn
  object host extends StringColumn


  override def fromRow(row: Row): Event = {
    Event(
      date(row),
      time(row),
      action(row),
      service(row),
      host(row)
    )
  }

  def getByEventDate(date: Date): Future[List[Event]] = {
    select
      .where(_.date eqs date)
      .consistencyLevel_=(ConsistencyLevel.ONE)
        .fetch()
  }

//  def getEventsByDateAfter(date: Date): Future[List[Event]] = {
//    select
//      .where(_.date eqs date)
//      .consistencyLevel_=(ConsistencyLevel.ONE)
//      .fetch()
//  }

  def getEventsByDateTime(date: Date, time: Long): Future[Option[Event]] = {
    select
      .where(_.date eqs date).and(_.time eqs time)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()
  }

  def deleteByDate(date: Date): Future[ResultSet] = {
    delete
      .where(_.date eqs date)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }

  def deleteByDateAndTime(date: Date, time: Long): Future[ResultSet] = {
    delete
      .where(_.date eqs date).and(_.time eqs time)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }
}