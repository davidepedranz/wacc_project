package authentication

import scala.util.Try

trait Authentication {

  def generateToken(username: String): String

  def parseToken(token: String): Try[String]
}
