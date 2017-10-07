package models

import play.api.libs.json._
import reactivemongo.bson.Macros.Annotations.Key
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros}

case class User(@Key("_id") username: String, permissions: Set[String])

object User {
  implicit val writes: OWrites[User] = Json.writes[User]

  implicit val bsonRead: BSONDocumentReader[User] = Macros.reader[User]
  implicit val bsonWrite: BSONDocumentWriter[User] = Macros.writer[User]
}
