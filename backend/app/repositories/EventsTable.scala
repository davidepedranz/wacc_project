package repositories

import java.util.Date

import com.outworkers.phantom.dsl._
import models.Event

import scala.concurrent.Future

/**
  * Define the structure of the tables for teh events in Cassandra.
  */
abstract class EventsTable extends Table[EventsTable, Event] {

  override def tableName: String = "events"

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

  def getEventsByDateTime(date: Date, time: Long): Future[Option[Event]] = {
    select
      .where(_.date eqs date).and(_.time eqs time)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()
  }
}