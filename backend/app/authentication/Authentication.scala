package authentication

import scala.util.Try

/**
  * Define the APIs needed to authenticate the user using some sort of token.
  */
trait Authentication {

  /**
    * Generate a new token for the given user.
    *
    * @param username Username.
    * @return Token.
    */
  def generateToken(username: String): String

  /**
    * Parse the token and tries to extract the user.
    *
    * @param token Raw token to parse.
    * @return A try that contains the username if the token is valid.
    */
  def parseToken(token: String): Try[String]
}
