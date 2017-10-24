package startup

import javax.inject.{Inject, Singleton}

import repositories.EventsRepository

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

@Singleton
final class BootstrapEventsRepository @Inject()(implicit ec: ExecutionContext, eventsRepository: EventsRepository) {

  // create tables in Cassandra
  eventsRepository.start()
}
