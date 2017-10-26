package startup

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment, Mode}

final class StartupModule(val environment: Environment, val configuration: Configuration) extends AbstractModule with ScalaModule {

  override def configure(): Unit = {

    // bootstrap events repository
    bind(classOf[BootstrapEventsRepository]).asEagerSingleton()

    // bootstrap users repository
    if (environment.mode != Mode.Test) {
      bind(classOf[BootstrapUsersRepository]).asEagerSingleton()
    }

    // TODO: make a runner
    bind(classOf[BootstrapEventsConsumer]).asEagerSingleton()
  }
}
