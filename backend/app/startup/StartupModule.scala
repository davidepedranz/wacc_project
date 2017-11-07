package startup

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment, Mode}

/**
  * Guice module to register some actions to perform when the backend is created, such as initialing the databases.
  * This is a separate module since it is used only in the backend and not in the workers. This allows to automatically
  * deploy the software, without any manual intervention to initialize the databases.
  */
final class StartupModule(val environment: Environment, val configuration: Configuration) extends AbstractModule with ScalaModule {

  override def configure(): Unit = {

    // bootstrap events repository
    bind(classOf[BootstrapEventsRepository]).asEagerSingleton()

    // bootstrap users repository
    if (environment.mode != Mode.Test) {
      bind(classOf[BootstrapUsersRepository]).asEagerSingleton()
    }
  }
}
