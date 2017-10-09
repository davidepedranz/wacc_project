import javax.inject._

import authentication.{Authentication, JwtAuthentication, Secret}
import authorization.{Authorization, DefaultHandlerCache}
import be.objectify.deadbolt.scala.DeadboltHandler
import be.objectify.deadbolt.scala.cache.HandlerCache
import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import repositories.{MongoUsersRepository, UsersRepository}
import v1.post._

class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {

  override def configure(): Unit = {

    // bootstrap
    bind(classOf[Bootstrap]).asEagerSingleton()

    // authentication
    val secret: String = ConfigFactory.load().getString("play.http.secret.key")
    bind(classOf[String]).annotatedWith(classOf[Secret]).toInstance(secret)
    bind[Authentication].to[JwtAuthentication]

    // authorization (deadbolt)
    bind[DeadboltHandler].to[Authorization]
    bind[HandlerCache].to[DefaultHandlerCache].in[Singleton]

    // users
    bind[UsersRepository].to[MongoUsersRepository].in[Singleton]


    // to remove
    bind[PostRepository].to[PostRepositoryImpl].in[Singleton]
  }
}
