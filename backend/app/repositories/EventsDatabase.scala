package repositories

import java.util.{Date, TimeZone}

import Connector.{connector, _}
import models._
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import play.api.Logger

import scala.util.control.Breaks._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import java.text.SimpleDateFormat

/**
  * This is our Database object that wraps our two existing tables,
  * giving the ability to receive different connectors
  * for example: One for production and other for testing
  */

//@Singleton
class EventsDatabase (override val connector: CassandraConnection) extends Database[EventsDatabase](connector) {
  object EventModel extends EventModel with connector.Connector

  def convertDateToDateOnly(date: Date): Date ={
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    sdf.setTimeZone(TimeZone.getTimeZone("ECT"))
    Logger.debug((sdf.parse(sdf.format(date))).toString)
    return sdf.parse(sdf.format(date))
  }
  /**
    * Save/update an event
    *
    * @param event
    * @return
    */
  def saveOrUpdate(event: Event): Future[ResultSet] = {
    val event2 = event.copy(date = convertDateToDateOnly(event.date))
    for {
      byEvent <- EventModel.store(event2).future
    } yield byEvent
  }

  /**
    * read an event
    *
    * @param date
    * @return
    */
  def read(date: Date): Future[List[Event]] = {
    for {
      byEvents <- EventModel.getByEventDate(convertDateToDateOnly(date))
    } yield byEvents
  }

//  def readAfter(date: Date): Future[List[Event]] = {
//    for {
//      byEvents <- EventModel.getEventsByDateAfter(convertDateToDateOnly(date))
//    } yield byEvents
//  }

  def readByDatetime(date: Date, time: Long): Future[Option[Event]] = {
    for {
      byEvents <- EventModel.getEventsByDateTime(convertDateToDateOnly(date), time)
    } yield byEvents
  }

  /**
    * Delete a event
    *
    * @param event
    * @return
    */
  def delete(event: Event): Future[ResultSet] = {
    for {
      byEvent <- EventModel.deleteByDate(convertDateToDateOnly(event.date))
    } yield byEvent
  }

  def deleteById(date: Date): Future[ResultSet] = {
    for {
      byEvent <- EventModel.deleteByDate(convertDateToDateOnly(date))
    } yield byEvent
  }

  def deleteByDatetime(date: Date, time: Long): Future[ResultSet] = {
    for {
      byEvents <- EventModel.deleteByDateAndTime(convertDateToDateOnly(date), time)
    } yield byEvents
  }

  def start()={
    Logger.debug("Create table if not exist")
    import com.datastax.driver.core.exceptions.NoHostAvailableException
    import java.util.concurrent.TimeUnit
    var lastException = new Exception
    var retryCount = 100
    breakable {
      while ( {
        retryCount > 0
      }) try {
        Await.ready(EventModel.create.ifNotExists().future(), Duration.Inf)
        break
      }catch {
        case e: Exception =>
          lastException = e
          retryCount = retryCount - 1
          Thread.sleep(TimeUnit.SECONDS.toMillis(5))
      }
    }
  }
}

/**
  * This is the database, it connects to a cluster with multiple contact points
  */
object eventDatabase extends EventsDatabase(connector)