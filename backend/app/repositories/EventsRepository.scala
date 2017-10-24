package repositories

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}
import javax.inject.{Inject, Singleton}

import com.outworkers.phantom.dsl._
import models._
import play.api.Logger
import services.Cassandra

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.control.Breaks._

@Singleton
final class EventsRepository @Inject()(cassandra: Cassandra) extends Database[EventsRepository](cassandra.connector) {

  object EventsTable$ extends EventsTable with connector.Connector

  def convertDateToDateOnly(date: Date): Date = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    sdf.setTimeZone(TimeZone.getTimeZone("ECT"))
    //    Logger.debug(sdf.parse(sdf.format(date)).toString)
    sdf.parse(sdf.format(date))
  }

  /**
    * Save or update an event.
    *
    * @param event Event to update.
    * @return Future that represents the request.
    */
  def saveOrUpdate(event: Event): Future[ResultSet] = {
    val event2 = event.copy(date = convertDateToDateOnly(event.date))
    for {
      byEvent <- EventsTable$.store(event2).future
    } yield byEvent
  }

  def read(date: Date): Future[List[Event]] = {
    for {
      byEvents <- EventsTable$.getByEventDate(convertDateToDateOnly(date))
    } yield byEvents
  }

  //  def readAfter(date: Date): Future[List[Event]] = {
  //    for {
  //      byEvents <- EventModel.getEventsByDateAfter(convertDateToDateOnly(date))
  //    } yield byEvents
  //  }

  def readByDatetime(date: Date, time: Long): Future[Option[Event]] = {
    for {
      byEvents <- EventsTable$.getEventsByDateTime(convertDateToDateOnly(date), time)
    } yield byEvents
  }

  //  /**
  //    * Delete a event
  //    *
  //    * @param event
  //    * @return
  //    */
  //  def delete(event: Event): Future[ResultSet] = {
  //    for {
  //      byEvent <- EventModel.deleteByDate(convertDateToDateOnly(event.date))
  //    } yield byEvent
  //  }
  //
  //  def deleteById(date: Date): Future[ResultSet] = {
  //    for {
  //      byEvent <- EventModel.deleteByDate(convertDateToDateOnly(date))
  //    } yield byEvent
  //  }
  //
  //  def deleteByDatetime(date: Date, time: Long): Future[ResultSet] = {
  //    for {
  //      byEvents <- EventModel.deleteByDateAndTime(convertDateToDateOnly(date), time)
  //    } yield byEvents
  //  }

  def start(): Unit = {
    Logger.info("Initialize events table in Cassandra.")
    import java.util.concurrent.TimeUnit
    var lastException = new Exception
    var retryCount = 100
    breakable {
      while ( {
        retryCount > 0
      }) try {
        Await.ready(EventsTable$.create.ifNotExists().future(), Duration.Inf)
        break
      } catch {
        case e: Exception =>
          lastException = e
          retryCount = retryCount - 1
          Thread.sleep(TimeUnit.SECONDS.toMillis(5))
      }
    }
  }
}
