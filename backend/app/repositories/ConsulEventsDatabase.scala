package repositories

import Connector.{connector, _}
import models._
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import play.api.Logger

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * This is our Database object that wraps our two existing tables,
  * giving the ability to receive different connectors
  * for example: One for production and other for testing
  */

//@Singleton
class ConsulEventsDatabase (override val connector: CassandraConnection) extends Database[ConsulEventsDatabase](connector) {
  object ConsulEventModel extends ConsulEventModel with connector.Connector
//  ConsulEventsDatabase(connector)

  /**
    * Save/update an event
    *
    * @param events
    * @return
    */
  def saveOrUpdate(events: ConsulEvent): Future[ResultSet] = {
    for {
      byEvent <- ConsulEventModel.store(events).future
    } yield byEvent
  }

  /**
    * read an event
    *
    * @param id
    * @return
    */
  def read(id: String): Future[List[ConsulEvent]] = {
    for {
      byEvents <- ConsulEventModel.getByConsulEventId(id)
    } yield byEvents
  }

  def readByDatetime(id: String, datetime: DateTime): Future[Option[ConsulEvent]] = {
    for {
      byEvents <- ConsulEventModel.getByConsulEventIdAndTime(id, datetime)
    } yield byEvents
  }

  /**
    * Delete a event
    *
    * @param event
    * @return
    */
  def delete(event: ConsulEvent): Future[ResultSet] = {
    for {
      byEvent <- ConsulEventModel.deleteById(event.id)
    } yield byEvent
  }

  def deleteById(id: String): Future[ResultSet] = {
    for {
      byEvent <- ConsulEventModel.deleteById(id)
    } yield byEvent
  }

  def deleteByDatetime(id: String, datetime: DateTime): Future[ResultSet] = {
    for {
      byEvents <- ConsulEventModel.deleteByConsulEventIdAndTime(id, datetime)
    } yield byEvents
  }

  def start()={
    Logger.debug("Create table if not exist")
    Await.ready(ConsulEventModel.create.ifNotExists().future(), Duration.Inf)
  }
}

/**
  * This is the database, it connects to a cluster with multiple contact points
  */
object consulEventDatabase extends ConsulEventsDatabase(connector)