package startup

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.Logger
import repositories.EventsRepository
import services.Retry

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Initialize the tables in Cassandra.
  */
@Singleton
final class BootstrapEventsRepository @Inject()(implicit ec: ExecutionContext, system: ActorSystem, repository: EventsRepository) {

  // try to initialize the tables in Cassandra
  Retry.periodically(
    repository.initialize(),
    1.seconds,
    (delay, ex) => Logger.error(s"Cannot connect to Cassandra to initialize the tables... (${ex.getMessage}). Retry in $delay...")
  )(ec, system.scheduler)
}
