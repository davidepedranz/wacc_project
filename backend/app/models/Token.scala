package models

import play.api.libs.json.{Json, OWrites}

case class Token(token: String, user: User)

object Token {
  implicit final val writes: OWrites[Token] = Json.writes[Token]
}
