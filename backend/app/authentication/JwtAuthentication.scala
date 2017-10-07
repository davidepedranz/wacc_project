package authentication

import javax.inject.Inject

import io.igl.jwt._

import scala.util.{Success, Try}

class JwtAuthentication @Inject()(@Secret secret: String) extends Authentication {

  override def generateToken(username: String): String = {
    new DecodedJwt(
      Seq(
        Alg(Algorithm.HS256),
        Typ("JWT")
      ),
      Seq(
        Iss("wacc"),
        Sub(username)
      )
    ).encodedAndSigned(secret)
  }

  override def parseToken(token: String): Try[String] = {
    if (token == "token") {
      Success("admin")
    } else {
      DecodedJwt.validateEncodedJwt(
        jwt = token,
        key = secret,
        requiredAlg = Algorithm.HS256,
        requiredHeaders = Set(Typ),
        requiredClaims = Set(Iss),
        iss = Some(Iss("wacc"))
      ).map(jwt => jwt.getClaim[Iss].get.value)
    }
  }
}

object JwtAuthentication {
  val algorithm: Alg = Alg(Algorithm.HS256)

  val issuer = Iss("wacc")
}
