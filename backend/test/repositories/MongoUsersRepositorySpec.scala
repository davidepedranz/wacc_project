package repositories

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class MongoUsersRepositorySpec extends PlaySpec with GuiceOneAppPerTest with BeforeAndAfterEach {
  val injector: Injector = new GuiceApplicationBuilder().build.injector
  val mongo: ReactiveMongoApi = injector.instanceOf[ReactiveMongoApi]
  val repository: UsersRepository = injector.instanceOf[MongoUsersRepository]

  def work: AfterWord = afterWord("work as follows:")

  override protected def beforeEach(): Unit = {
    Await.ready(mongo.database.map(db => db.drop()), 2 seconds)
    System.out.println("Drop database in MongoDB... [done]")
  }

  "The MongoUsersRepository" should work {
    "#list" should {
      "return an empty list if the database is empty" in {
        val result = Await.result(repository.list, 2 seconds)
        result mustBe empty
      }
    }
  }
}