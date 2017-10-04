package models

import com.google.inject.ImplementedBy
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

case class Credentials(username: String, password: String)

object Credentials {
  implicit val credentialsWrites: OWrites[Credentials] = Json.writes[Credentials]
  implicit val credentialsReads: Reads[Credentials] = (
    (JsPath \ "username").read[String](minLength[String](5) keepAnd maxLength[String](20)) and
      (JsPath \ "password").read[String](minLength[String](10) keepAnd maxLength[String](100))
    ) (Credentials.apply _)
}

case class User(id: String, credentials: Credentials, permissions: Set[String])

object User {
  implicit val usersWrites: OWrites[User] = Json.writes[User]
  implicit val usersReads: Reads[User] = (
    (JsPath \ "id").read[String](minLength[String](10)) and
      (JsPath \ "credentials").read[Credentials] and
      (JsPath \ "permissions").read[Set[String]]
    ) (User.apply _)
}

@ImplementedBy(classOf[InMemoryUsersRepository])
trait UsersRepository {
  def list: Future[Set[User]]
}

class InMemoryUsersRepository extends UsersRepository {
  private val users = TrieMap.empty[String, User]
  users.put("admin", User(
    "admin",
    Credentials(
      "admin",
      "password"
    ),
    Set(
      "read-x"
    )
  ))

  def list: Future[Set[User]] = {
    Future.successful(users.values.to[Set])
  }
}
