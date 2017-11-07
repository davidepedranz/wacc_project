package authentication

import scala.util.Try

/**
  * Define the APIs needed to authenticate the user using some sort of token.
  */
trait Authentication {

  /**
    * Header were to look for the JWT token.
    */
  val AUTHENTICATION_HEADER = "Authorization"

  /**
    * Parse the Authorization header to extract the JWT token.
    * NB: we do this by hand as recommended from the official documentation:
    * https://www.playframework.com/documentation/2.6.x/ScalaActionsComposition#Authentication
    *
    * @param header Authorization header.
    * @return Option with the serialized JWT token, if present.
    */
  def parseAuthorizationHeader(header: String): Option[String]

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
