package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{maxLength, minLength, verifying, _}
import play.api.libs.json.{JsPath, Reads}
import reactivemongo.bson.Macros.Annotations.Key
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros}

final case class UserWithPassword(@Key("_id") username: String, password: String, permissions: Set[String])

object UserWithPassword {
  implicit final val reads: Reads[UserWithPassword] = (
    (JsPath \ "username").read[String](minLength[String](5) keepAnd maxLength[String](30)) and
      (JsPath \ "password").read[String](minLength[String](8) keepAnd maxLength[String](100)) and
      (JsPath \ "permissions").read[Set[String]](verifying[Set[String]](permissions => permissions.forall(Permission.validate)))
    ) (UserWithPassword.apply _)

  implicit final val bsonRead: BSONDocumentReader[UserWithPassword] = Macros.reader[UserWithPassword]
  implicit final val bsonWrite: BSONDocumentWriter[UserWithPassword] = Macros.writer[UserWithPassword]

  final val DEFAULT_USER = UserWithPassword("admin", "password", Permission.ALL)
}
