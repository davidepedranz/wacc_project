package authentication

import javax.inject.Inject

import io.igl.jwt._

import scala.util.Try

/**
  * Implements the user authentication using JSON Web Token.
  *
  * @param secret Secret key used to sign the JWT.
  */
final class JwtAuthentication @Inject()(@Secret secret: String) extends Authentication {

  // JWT header section
  private val typ: String = "JWT"
  private val algorithm: Algorithm = Algorithm.HS256

  // JWT body section
  private val issuer: Iss = Iss("wacc")

  // TODO: add claims for duration etc

  override def generateToken(username: String): String = {
    new DecodedJwt(
      Seq(
        Typ(typ),
        Alg(algorithm)
      ),
      Seq(
        issuer,
        Sub(username)
      )
    ).encodedAndSigned(secret)
  }

  override def parseToken(token: String): Try[String] = {
    DecodedJwt.validateEncodedJwt(
      jwt = token,
      key = secret,
      requiredAlg = algorithm,
      requiredHeaders = Set(Typ),
      requiredClaims = Set(Iss, Sub),
      iss = Some(issuer)
    ).map(jwt => jwt.getClaim[Sub].get.value)
  }
}
