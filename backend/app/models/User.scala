package models

import play.api.libs.json._
import reactivemongo.bson.Macros.Annotations.Key
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros}

final case class User(@Key("_id") username: String, permissions: Set[String])

object User {
  implicit final val writes: OWrites[User] = Json.writes[User]

  implicit final val bsonRead: BSONDocumentReader[User] = Macros.reader[User]
  implicit final val bsonWrite: BSONDocumentWriter[User] = Macros.writer[User]
}
