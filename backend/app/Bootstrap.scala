import javax.inject._

import models.UserWithPassword
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import repositories.UsersRepository

import scala.concurrent.ExecutionContext

@Singleton
final class Bootstrap @Inject()(implicit ec: ExecutionContext, lifecycle: ApplicationLifecycle, usersRepository: UsersRepository) {

  // if not user is present, create a default one
  usersRepository.list.map { users =>
    if (users.isEmpty) {
      Logger.warn("The database of users is empty... creating the default user.")
      usersRepository.create(UserWithPassword.DEFAULT_USER)
    } else {
      Logger.debug("The database of users is not empty... skip creating the default user.")
    }
  }
}
