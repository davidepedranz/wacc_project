package authentication

import java.util.UUID
import javax.inject.{Inject, Named}

import io.igl.jwt._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

/**
  * Implements the user authentication using JSON Web Token.
  *
  * @param secret Secret key used to sign the JWT.
  */
final class JwtAuthentication @Inject()(@Named("secret") secret: String) extends Authentication {

  // JWT header
  private val JWT_TYPE: String = "JWT"
  private val JWT_ALGORITHM: Algorithm = Algorithm.HS256

  // JWT body
  private val APPLICATION_NAME: String = "wacc"
  private val NOT_BEFORE_LEEWAY_SECONDS: Long = (2 minutes).toSeconds
  private val DURATION_SECONDS: Long = (7 days).toSeconds

  override def generateToken(username: String): String = {
    val now: Long = System.currentTimeMillis() / 1000
    new DecodedJwt(
      Seq(
        Typ(JWT_TYPE),
        Alg(JWT_ALGORITHM)
      ),
      Seq(
        Jti(UUID.randomUUID().toString),
        Iss(APPLICATION_NAME),
        Aud(APPLICATION_NAME),
        Sub(username),
        Nbf(now - NOT_BEFORE_LEEWAY_SECONDS),
        Iat(now),
        Exp(now + DURATION_SECONDS)
      )
    ).encodedAndSigned(secret)
  }

  override def parseToken(token: String): Try[String] = {
    DecodedJwt.validateEncodedJwt(
      jwt = token,
      key = secret,
      requiredAlg = JWT_ALGORITHM,
      requiredHeaders = Set(Typ),
      requiredClaims = Set(Jti, Iss, Aud, Sub, Nbf, Iat, Exp),
      iss = Some(Iss(APPLICATION_NAME)),
      aud = Some(Aud(APPLICATION_NAME))
    ).map(jwt => jwt.getClaim[Sub].get.value)
  }

  override def parseAuthorizationHeader(header: String): Option[String] = {
    header.split("""\s""") match {
      case Array("Bearer", token) => Option(token)
      case _ ⇒ None
    }
  }
}
