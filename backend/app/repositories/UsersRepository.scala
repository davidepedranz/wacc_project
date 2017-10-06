package repositories

import models.{User, UserWithPassword}

import scala.concurrent.Future
import scalaz.\/

sealed trait UserError

final case class DuplicateUser() extends UserError

trait UsersRepository {
  def create(user: UserWithPassword): Future[DuplicateUser \/ Unit]

  def list: Future[Seq[User]]
}
