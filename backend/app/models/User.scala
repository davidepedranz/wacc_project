package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import reactivemongo.bson.Macros.Annotations.Key
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros}

sealed trait BaseUser {
  def username: String

  def permissions: Set[String]
}

case class User(@Key("_id") username: String, permissions: Set[String]) extends BaseUser

object User {
  implicit val writes: OWrites[User] = Json.writes[User]

  implicit val bsonRead: BSONDocumentReader[User] = Macros.reader[User]
  implicit val bsonWrite: BSONDocumentWriter[User] = Macros.writer[User]
  //  implicit val handler: BSONHandler[BSONDocument, User] = Macros.handler[User]
}

case class UserWithPassword(@Key("_id") username: String, password: String, permissions: Set[String]) extends BaseUser

object UserWithPassword {
  implicit val reads: Reads[UserWithPassword] = (
    (JsPath \ "username").read[String](minLength[String](5) keepAnd maxLength[String](30)) and
      (JsPath \ "password").read[String](minLength[String](5) keepAnd maxLength[String](100)) and
      (JsPath \ "permissions").read[Set[String]]
    ) (UserWithPassword.apply _)

  implicit val bsonRead: BSONDocumentReader[UserWithPassword] = Macros.reader[UserWithPassword]
  implicit val bsonWrite: BSONDocumentWriter[UserWithPassword] = Macros.writer[UserWithPassword]
  //  implicit val handler: BSONHandler[BSONDocument, UserWithPassword] = Macros.handler[UserWithPassword]
}




