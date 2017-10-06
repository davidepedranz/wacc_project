package repositories

import javax.inject.{Inject, Singleton}

import models.{User, UserWithPassword}
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MongoUsersRepository @Inject()(implicit ec: ExecutionContext, val reactiveMongoApi: ReactiveMongoApi)
  extends UsersRepository with ReactiveMongoComponents {

  private val USERS_COLLECTION = "users"

  private def usersCollection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection(USERS_COLLECTION))

  override def list: Future[Seq[User]] = {
    usersCollection.flatMap(_
      .find(BSONDocument())
      .sort(BSONDocument("_id" -> 1))
      .cursor[User](ReadPreference.primary)
      .collect[Seq](-1, Cursor.FailOnError[Seq[User]]())
    )
  }

  override def create(user: UserWithPassword): Future[Boolean] = {
    usersCollection.flatMap(_.insert(user))
      .map(result => result.ok)
  }
}
