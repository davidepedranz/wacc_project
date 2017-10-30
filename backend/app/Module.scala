import javax.inject._

import authentication.{Authentication, JwtAuthentication}
import authorization.{Authorization, AuthorizationCache}
import be.objectify.deadbolt.scala.DeadboltHandler
import be.objectify.deadbolt.scala.cache.HandlerCache
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import repositories.{EventsRepository, MongoUsersRepository, UsersRepository}
import services.{Cassandra, Kafka}

// see startup.StartupModule for db initializations
final class Module(val environment: Environment, val configuration: Configuration) extends AbstractModule with ScalaModule {

  override def configure(): Unit = {

    // Kafka utilities
    bind(classOf[Kafka])

    // authentication
    val secret: String = configuration.get[String]("play.http.secret.key")
    bind(classOf[String]).annotatedWith(Names.named("secret")).toInstance(secret)
    bind[Authentication].to[JwtAuthentication]

    // authorization (deadbolt)
    bind[DeadboltHandler].to[Authorization]
    bind[HandlerCache].to[AuthorizationCache].in[Singleton]

    // users (Mongo)
    bind[UsersRepository].to[MongoUsersRepository].in[Singleton]

    // events (Cassandra)
    bind(classOf[Cassandra])
    bind(classOf[EventsRepository])
  }
}
