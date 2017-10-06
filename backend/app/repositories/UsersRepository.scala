package repositories

import models.{User, UserWithPassword}

import scala.concurrent.Future
import scalaz.\/

/**
  * Base trait for errors caused by operations on the UsersRepository.
  */
sealed trait UserError

final case class DuplicateUser() extends UserError

/**
  * Trait for the users' repository.
  */
trait UsersRepository {

  /**
    * Try to match a user in the repository with the given username and password.
    *
    * @param username Username.
    * @param password Password.
    * @return Future containing the user if found, empty otherwise.
    */
  def authenticate(username: String, password: String): Future[Option[User]]

  /**
    * Create a new user in the repository. Users are required to have an unique identifiers,
    * to this method might return an error in case of duplicated users.
    *
    * @param user The new user to insert.
    * @return A future that can either return an error or nothing (if the operation is successful).
    */
  def create(user: UserWithPassword): Future[DuplicateUser \/ Unit]

  /**
    * Retrieve the list of users present in the repository, sorted for ascending username.
    *
    * @return List of users, in ascending order by username.
    */
  def list: Future[Seq[User]]

}
