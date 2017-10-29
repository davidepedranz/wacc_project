
package controllers

import javax.inject._

import be.objectify.deadbolt.scala.ActionBuilders
import be.objectify.deadbolt.scala.models.PatternType
import com.typesafe.config.ConfigFactory
import models.{Permission, Service, Task, ServiceScale}
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ComponentsController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, config: Configuration,
                                     actionBuilders: ActionBuilders, bodyParsers: PlayBodyParsers, ws: WSClient) extends AbstractController(cc) {

  private val host = ConfigFactory.load().getString("docker_host")
  private val frontend_host = ConfigFactory.load().getString("frontend_host")
  private val getContainersUrl = "/containers/json"
  private val getContainersAllUrl = "/containers/json?all=true"
  private val getImagesUrl = "/images/json"

  // authorize calls to the Docker Swarm APIs
  private val servicesReadPermission: actionBuilders.PatternAction.PatternActionBuilder = {
    actionBuilders.PatternAction(value = Permission.SERVICES, patternType = PatternType.EQUALITY)
  }


  def getContainers = Action.async {
    val request = ws.url(host + getContainersUrl)
    request.get().map {
      response => Ok(response.body)
    }
  }


  def getContainersAll = Action.async {
    val request = ws.url(host + getContainersAllUrl)
    request.get().map {
      response => Ok(response.body)
    }
  }

  def getContainersInfo(id: String) = Action.async {
    val request = ws.url(host + "/containers/" + id + "/json")
    request.get().map {
      response => Ok(response.body)
    }
  }

  def getImages = Action.async {
    val request = ws.url(host + getImagesUrl)
    request.get().map {
      response => Ok(response.body)
    }
  }


  def postContainerStop(id: String) = Action.async {
    val request = ws.url(host + "/containers/" + id + "/stop")
    request.post("").map {
      response => Ok(response.body)
    }
  }


  def postContainerStart(id: String) = Action.async {
    val request = ws.url(host + "/containers/" + id + "/start")
    request.post("").map {
      response => Ok(response.body)
    }
  }


  def createService = Action.async(parse.json) {
    implicit request =>
    val bodyAsJson = request.body
    bodyAsJson.validate[ServiceScale] match {
      case success : JsSuccess[ServiceScale] =>{
      ws.url(host + "/services/create")
      .withHttpHeaders("Accept" -> "application/json").post(bodyAsJson).map {
        response =>
        response.body contains "ID" match {
          case true => Ok(response.body)
          case false => Conflict("Service already exist")
          }
        }
      }
      case JsError(error) => Future(BadRequest(JsError.toJson(error)))
    }
  }


  def updateService(id: String) = Action.async(parse.json) { request =>
    ws.url(host + "/services/" + id)
      .get()
      .flatMap { response =>
        val version: String = Json.stringify((Json.parse(response.body) \ "Version" \ "Index").get)
        request.body.validate[ServiceScale] match {
          case success : JsSuccess[ServiceScale] => {
            val updateURL = ws.url(host + "/services/" + id + "/update?version=" + version)
            updateURL.withHttpHeaders("Accept" -> "application/json").post(request.body).map {
              response =>
              Ok(response.body)
            }
          }
          case JsError(error) => Future(BadRequest(JsError.toJson(error)))
        }
      }
  }

  def deleteService(id: String) = Action.async {
    request =>
      val deleteServiceURL = ws.url(host + "/services/" + id)
      deleteServiceURL.execute("DELETE").map {
        response => Ok(response.body)
      }
  }

  // TODO: cleanup
  def list: Action[AnyContent] = servicesReadPermission.defaultHandler() {

    val services: Future[Seq[Service]] = ws.url(host + "/services")
      .get()
      .map(response => response.json.as[Seq[Service]])

    val counts: Future[Map[String, Int]] = ws.url(host + "/tasks")
      .get()
      .map(response => response.json.as[Seq[Task]])
      .map(tasks => tasks.filter(_.status == "running").map(_.service).groupBy(identity).mapValues(_.size))

    for {
      services <- services
      counts <- counts
    } yield {
      val ok = services.map {
        service => service.copy(current = counts.getOrElse(service.id, 0))
      }
      Logger.debug(ok.toString)
      Ok(Json.toJson(ok))
    }
  }
}
