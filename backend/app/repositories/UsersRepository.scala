package repositories

import models.{Credentials, User, UserWithPassword}

import scala.concurrent.Future
import scalaz.\/

/**
* Base trait for errors caused by operations on the UsersRepository.
*/
sealed trait UserError

final case class DuplicateUser() extends UserError

final case class UserNotFound() extends UserError


/**
  * Trait for the users' repository.
  */
trait UsersRepository {

  /**
    * Try to match a user in the repository with the given username and password.
    *
    * @param credentials Username and password.
    * @return Future containing the user if found, empty otherwise.
    */
  def authenticate(credentials: Credentials): Future[Option[User]]

  /**
    * Create a new user in the repository. Users are required to have an unique identifiers,
    * to this method might return an error in case of duplicated users.
    *
    * @param user The new user to insert.
    * @return Future containing either an error or nothing (if the operation is successful).
    */
  def create(user: UserWithPassword): Future[DuplicateUser \/ Unit]

  /**
    * Retrieve the list of users present in the repository, sorted for ascending username.
    *
    * @return List of users, in ascending order by username.
    */
  def list: Future[Seq[User]]

  /**
    * Retrieve the user with the given username from the repository.
    *
    * @param username Username.
    * @return Future containing the user if found, empty otherwise.
    */
  def read(username: String): Future[Option[User]]

  /**
    * Delete the user with the specified username from the repository. If there is no user
    * with the given username, this method has no effect.
    *
    * @param username Username of the user to delete.
    * @return Future that represent the async operation. No content is returned in case of success.
    */
  def delete(username: String): Future[UserNotFound \/ Unit]

  /**
    * Add a given permission to the specified user. The implementation should make sure not to store duplicates.
    *
    * @param username   Username.
    * @param permission Permission to add.
    * @return Future that represent the async operation. No content is returned in case of success.
    */
  def addPermission(username: String, permission: String): Future[UserNotFound \/ Unit]

  /**
    * Remove a given permission from the specified user.
    *
    * @param username   Username.
    * @param permission Permission to remove.
    * @return Future that represent the async operation. No content is returned in case of success.
    */
  def removePermission(username: String, permission: String): Future[UserNotFound \/ Unit]
}
