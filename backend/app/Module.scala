import javax.inject._

import authentication.{Authentication, JwtAuthentication, Secret}
import authorization.{Authorization, AuthorizationCache}
import be.objectify.deadbolt.scala.DeadboltHandler
import be.objectify.deadbolt.scala.cache.HandlerCache
import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import repositories.{MongoUsersRepository, UsersRepository}
import services.Kafka

final class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {

  override def configure(): Unit = {

    // bootstrap
    bind(classOf[Bootstrap]).asEagerSingleton()

    // kafka utilities
    bind(classOf[Kafka])

    // authentication
    val secret: String = ConfigFactory.load().getString("play.http.secret.key")
    bind(classOf[String]).annotatedWith(classOf[Secret]).toInstance(secret)
    bind[Authentication].to[JwtAuthentication]

    // authorization (deadbolt)
    bind[DeadboltHandler].to[Authorization]
    bind[HandlerCache].to[AuthorizationCache].in[Singleton]

    // users
    bind[UsersRepository].to[MongoUsersRepository].in[Singleton]
  }
}
