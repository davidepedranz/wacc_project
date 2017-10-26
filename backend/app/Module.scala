import javax.inject._

import authentication.{Authentication, JwtAuthentication, Secret}
import authorization.{Authorization, AuthorizationCache}
import be.objectify.deadbolt.scala.DeadboltHandler
import be.objectify.deadbolt.scala.cache.HandlerCache
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import repositories.{EventsRepository, MongoUsersRepository, UsersRepository}
import services.{Cassandra, Kafka}

/**
  * Main Guice module for the Play application. Binds traits with the implementations and
  * bootstrap the important components, like live connection to the Docker Swarm Events.
  */
final class Module(val environment: Environment, val configuration: Configuration) extends AbstractModule with ScalaModule {

  override def configure(): Unit = {

    // bootstrap
    //    bind(classOf[BootstrapEventsRepository]).asEagerSingleton()
    //    if (environment.mode != Mode.Test) {
    //      bind(classOf[BootstrapUsersRepository]).asEagerSingleton()
    //      //      bind(classOf[BootstrapEventsProducer]).asEagerSingleton()
    //      //      bind(classOf[BootstrapEventsConsumer]).asEagerSingleton()
    //    }

    // Kafka utilities
    bind(classOf[Kafka])

    // authentication
    val secret: String = configuration.get[String]("play.http.secret.key")
    bind(classOf[String]).annotatedWith(classOf[Secret]).toInstance(secret)
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
