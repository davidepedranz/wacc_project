package repositories

import javax.inject.{Inject, Singleton}

import models.{Credentials, User, UserWithPassword}
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.BSONDocument
import reactivemongo.core.errors.DatabaseException

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/, \/-}

@Singleton
class MongoUsersRepository @Inject()(implicit ec: ExecutionContext, val reactiveMongoApi: ReactiveMongoApi)
  extends UsersRepository with ReactiveMongoComponents {

  private val COLLECTION_USERS = "users"
  private val FIELD_USERNAME = "_id"
  private val FIELD_PASSWORD = "password"
  private val FIELD_PERMISSIONS = "permissions"

  private def usersCollection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection(COLLECTION_USERS))

  override def authenticate(credentials: Credentials): Future[Option[User]] = {
    usersCollection.flatMap(_
      .find(BSONDocument(
        FIELD_USERNAME -> credentials.username,
        FIELD_PASSWORD -> credentials.password
      ))
      .one[User](ReadPreference.nearest)
    )
  }

  override def create(user: UserWithPassword): Future[DuplicateUser \/ Unit] = {
    usersCollection.flatMap(_.insert(user))
      .map(result => \/-(assert(result.ok)))
      .recoverWith {
        case _: DatabaseException => Future(-\/(DuplicateUser()))
      }
  }

  override def list: Future[Seq[User]] = {
    // NB: -1 in collect means "infinite" users
    usersCollection.flatMap(_
      .find(BSONDocument())
      .sort(BSONDocument("_id" -> 1))
      .cursor[User](ReadPreference.nearest)
      .collect[Seq](-1, Cursor.FailOnError[Seq[User]]())
    )
  }

  override def read(username: String): Future[Option[User]] = {
    usersCollection.flatMap(_
      .find(BSONDocument(FIELD_USERNAME -> username))
      .one[User](ReadPreference.nearest)
    )
  }

  override def delete(username: String): Future[UserNotFound \/ Unit] = {
    usersCollection
      .flatMap(_.remove(BSONDocument(FIELD_USERNAME -> username)))
      .map(result => result.n)
      .map {
        case 1 => \/-(Unit)
        case _ => -\/(UserNotFound())
      }
  }

  override def addPermission(username: String, permission: String): Future[UserNotFound \/ Unit] = {
    usersCollection.flatMap(_
      .findAndUpdate(
        BSONDocument(FIELD_USERNAME -> username),
        BSONDocument("$addToSet" -> BSONDocument(FIELD_PERMISSIONS -> permission))
      )
      .map(result => result.lastError.get.n)
      .map {
        case 1 => \/-(Unit)
        case _ => -\/(UserNotFound())
      }
    )
  }

  override def removePermission(username: String, permission: String): Future[UserNotFound \/ Unit] = {
    usersCollection.flatMap(_
      .findAndUpdate(
        BSONDocument(FIELD_USERNAME -> username),
        BSONDocument("$pull" -> BSONDocument(FIELD_PERMISSIONS -> permission))
      )
      .map(result => result.lastError.get.n)
      .map {
        case 1 => \/-(Unit)
        case _ => -\/(UserNotFound())
      }
    )
  }
}
