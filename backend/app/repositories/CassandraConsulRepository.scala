package repositories

import javax.inject.Singleton

import Connector._
import models._
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._

import scala.concurrent.Future

/**
  * This is our Database object that wraps our two existing tables,
  * giving the ability to receive different connectors
  * for example: One for production and other for testing
  */
@Singleton
class SongsDatabase(override val connector: CassandraConnection) extends Database[SongsDatabase](connector) {
  object SongsModel extends SongsModel with connector.Connector

  /**
    * Save a song in both tables
    *
    * @param songs
    * @return
    */
  def saveOrUpdate(songs: Song): Future[ResultSet] = {
    for {
      byArtist <- SongsModel.store(songs).future
    } yield byArtist
  }

  /**
    * Delete a song in both table
    *
    * @param song
    * @return
    */
  def delete(song: Song): Future[ResultSet] = {
    for {
      byArtist <- SongsModel.deleteById(song.id)
    } yield byArtist
  }
}

/**
  * This is the database, it connects to a cluster with multiple contact points
  */
object Database extends SongsDatabase(connector)