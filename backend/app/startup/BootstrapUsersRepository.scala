package startup

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import models.UserWithPassword
import play.Logger
import repositories.UsersRepository
import services.Retry

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps

@Singleton
final class BootstrapUsersRepository @Inject()(implicit ec: ExecutionContext, system: ActorSystem, repository: UsersRepository) {

  // log errors to connect to MongoDB
  val callback: (FiniteDuration, Throwable) => Any = {
    (delay, ex) => Logger.error(s"Cannot connect to MongoDB to create the first user... (${ex.getMessage}). Retry in $delay...")
  }

  // if not user is present, create a default one
  Retry.periodically(repository.list, 3.seconds, callback)(ec, system.scheduler).map { users =>
    if (users.isEmpty) {
      Logger.warn("The database of users is empty... creating the default user.")
      repository.create(UserWithPassword.DEFAULT_USER)
    } else {
      Logger.info("The database of users is not empty... skip creating the default user.")
    }
  }.recover {
    case ex => Logger.error("Cannot connect to MongoDB.", ex)
  }
}
