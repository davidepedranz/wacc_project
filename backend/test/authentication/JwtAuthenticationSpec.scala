package authentication

import org.scalatest.{MustMatchers, OptionValues, WordSpec}

import scala.util.Try

final class JwtAuthenticationSpec extends WordSpec with MustMatchers with OptionValues {

  val wrongAuthenticator: Authentication = new JwtAuthentication("wrong-secret")
  val authenticator: Authentication = new JwtAuthentication("secret")

  "JwtAuthentication" when {
    "#generateToken" should {
      "should generate valid tokens" in {
        val token: String = authenticator.generateToken("username")
        val result: Try[String] = authenticator.parseToken(token)
        result mustBe 'success
        result.get mustBe "username"
      }
    }

    "#parseToken" should {
      "reject random strings" in {
        val result: Try[String] = authenticator.parseToken("random-string")
        result mustBe 'failure
      }
      "reject token with wrong signature" in {
        val token: String = wrongAuthenticator.generateToken("username")
        val result: Try[String] = authenticator.parseToken(token)
        result mustBe 'failure
      }
      "correctly parse generated token" in {
        val token: String = authenticator.generateToken("username")
        val result: Try[String] = authenticator.parseToken(token)
        result mustBe 'success
        result.get mustBe "username"
      }
    }

    "#parseAuthorizationHeader" should {
      "return an empty option if the token is missing" in {
        val result = authenticator.parseAuthorizationHeader("Bearer")
        result mustBe empty
      }
      "return the token if present" in {
        val result = authenticator.parseAuthorizationHeader("Bearer fake-token")
        result mustBe defined
        result must contain("fake-token")
      }
    }
  }
}
