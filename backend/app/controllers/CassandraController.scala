package controllers

import javax.inject._

import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltHandler}
import org.joda.time.DateTime
import play.api.Logger
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.Json
import play.api.mvc._
import repositories.consulEventDatabase

import scala.concurrent.ExecutionContext

@Singleton
final class CassandraController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, bodyParsers: PlayBodyParsers,
                                          handler: DeadboltHandler, actionBuilders: ActionBuilders) extends AbstractController(cc) {

  def list(id: String) = Action.async { implicit req =>
    Logger.debug("Called reading: " + id)
    consulEventDatabase.start()

    // read data
    for {
      ans <- consulEventDatabase.read(id)
    } yield {
      Ok(Json.toJson(ans))
    }
  }

  def test = Action.async { implicit req =>
    Logger.debug("Called")
    consulEventDatabase.start()
    val dt = DateTime.parse("2017-10-16 14:13:37", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

//    // save testing data
//    val r = scala.util.Random
//    val item = new ConsulEventItem(r.nextInt().toString, r.nextInt().toString, r.nextInt().toString, r.nextInt().toString, r.nextInt().toString, r.nextInt().toString, r.nextInt().toString, r.nextInt().toString)
//    val evt = new ConsulEvent("cassandra-test", item, dt)
//    val evt1 = new ConsulEvent("cassandra-test1", item, dt)
//    consulEventDatabase.saveOrUpdate(evt)
//    consulEventDatabase.saveOrUpdate(evt1)

    // read data
    for {
      ans <- consulEventDatabase.readByDatetime("cassandra-test", dt)
    } yield {
      Ok(ans.toString)
    }

////    Deleting
//    consulEventDatabase.deleteByDatetime("cassandra-test", dt)

  }


}