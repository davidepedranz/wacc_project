package repositories

import models.{User, UserWithPassword}

import scala.concurrent.Future

trait UsersRepository {
  def list: Future[Seq[User]]

  // TODO: make this signature better?
  def create(user: UserWithPassword): Future[Boolean]
}
