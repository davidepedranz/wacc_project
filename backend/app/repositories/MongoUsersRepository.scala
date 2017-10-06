package repositories

import javax.inject.{Inject, Singleton}

import models.{User, UserWithPassword}
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.BSONDocument
import reactivemongo.core.errors.DatabaseException

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/, \/-}

// TODO: write and read concerns!

@Singleton
class MongoUsersRepository @Inject()(implicit ec: ExecutionContext, val reactiveMongoApi: ReactiveMongoApi)
  extends UsersRepository with ReactiveMongoComponents {

  private val COLLECTION_USERS = "users"
  private val FIELD_USERNAME = "_id"
  private val FIELD_PASSWORD = "password"

  private def usersCollection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection(COLLECTION_USERS))

  override def authenticate(username: String, password: String): Future[Option[User]] = {
    usersCollection.flatMap(_
      .find(BSONDocument(
        FIELD_USERNAME -> username,
        FIELD_PASSWORD -> password
      ))
      .one[User]
    )
  }

  override def create(user: UserWithPassword): Future[DuplicateUser \/ Unit] = {
    usersCollection.flatMap(_.insert(user))
      .map(result => \/-(assert(result.ok)))
      .recoverWith {
        case _: DatabaseException => Future.successful(-\/(DuplicateUser()))
      }
  }

  override def list: Future[Seq[User]] = {
    usersCollection.flatMap(_
      .find(BSONDocument())
      .sort(BSONDocument("_id" -> 1))
      .cursor[User](ReadPreference.primary)
      .collect[Seq](-1, Cursor.FailOnError[Seq[User]]())
    )
  }

  override def read(username: String): Future[Option[User]] = {
    usersCollection.flatMap(_
      .find(BSONDocument(FIELD_USERNAME -> username))
      .one[User]
    )
  }

  override def delete(username: String): Future[Unit] = {
    usersCollection
      .flatMap(_.remove(BSONDocument(FIELD_USERNAME -> username)))
      .map(_ => None)
  }
}
