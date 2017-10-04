import javax.inject._

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import repositories.{MongoUsersRepository, UsersRepository}
import v1.post._

class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[PostRepository].to[PostRepositoryImpl].in[Singleton]

    bind[UsersRepository].to[MongoUsersRepository].in[Singleton]
  }
}
