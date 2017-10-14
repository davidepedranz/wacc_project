package repositories

import javax.inject.{Inject, Singleton}

import Connector._
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

class ConsulEventsDatabase (override val connector: CassandraConnection) extends Database[ConsulEventsDatabase](connector) {
  object ConsulEventModel extends ConsulEventModel with connector.Connector


  /**
    * Save a song in both tables
    *
    * @param events
    * @return
    */
  def saveOrUpdate(events: ConsulEvent): Future[ResultSet] = {
    for {
      byArtist <- ConsulEventModel.store(events).future
    } yield byArtist
  }

  /**
    * Delete a song in both table
    *
    * @param event
    * @return
    */
  def delete(event: ConsulEvent): Future[ResultSet] = {
    for {
      byArtist <- ConsulEventModel.deleteById(event.id)
    } yield byArtist
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