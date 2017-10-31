package repositories

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}
import javax.inject.{Inject, Singleton}

import com.outworkers.phantom.dsl._
import models._
import services.Cassandra

import scala.concurrent.Future

/**
  * Repository for the events, stored in Cassandra.
  */
@Singleton
final class EventsRepository @Inject()(cassandra: Cassandra) extends Database[EventsRepository](cassandra.connector) {

  object EventsTable$ extends EventsTable with connector.Connector

  /**
    * Create the table for the events if it does not exist already.
    */
  def initialize(): Future[Seq[ResultSet]] = {
    EventsTable$.create.ifNotExists().future()
  }

  /**
    * Remove all events from the database. This should be used for TESTING ONLY.
    */
  def _removeAllEvents(): Future[ResultSet] = {
    EventsTable$.truncate.future
  }

  /**
    * Save or update an event. If the event does not exist in the database,
    * it will be created. If the event is already present, it will be updated.
    */
  def saveOrUpdate(event: Event): Future[ResultSet] = {
    val event2 = event.copy(date = convertDateToDateOnly(event.date))
    EventsTable$.store(event2).future
  }

  /**
    * Read all the events for a given day.
    */
  def readByDate(date: Date): Future[List[Event]] = {
    EventsTable$.getByEventDate(convertDateToDateOnly(date))
  }

  private def convertDateToDateOnly(date: Date): Date = {
    //noinspection SpellCheckingInspection
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    sdf.setTimeZone(TimeZone.getTimeZone("ECT"))
    sdf.parse(sdf.format(date))
  }
}
