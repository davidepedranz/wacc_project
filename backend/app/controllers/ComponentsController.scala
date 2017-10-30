package controllers

import javax.inject._

import be.objectify.deadbolt.scala.ActionBuilders
import be.objectify.deadbolt.scala.models.PatternType
import models.{Permission, Service, ServiceScale, Task}
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ComponentsController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents,
                                     config: Configuration, actionBuilders: ActionBuilders,
                                     bodyParsers: PlayBodyParsers, ws: WSClient) extends AbstractController(cc) {

  // configuration
  private val host = config.get[String]("docker_host")

  private val getContainersUrl = "/containers/json"
  private val getContainersAllUrl = "/containers/json?all=true"
  private val getImagesUrl = "/images/json"

  // authorize calls to the Docker Swarm APIs
  private val servicesPermission: actionBuilders.PatternAction.PatternActionBuilder = {
    actionBuilders.PatternAction(value = Permission.SERVICES, patternType = PatternType.EQUALITY)
  }


  def getContainers: Action[AnyContent] = servicesPermission.defaultHandler() {
    val request = ws.url(host + getContainersUrl)
    request.get().map {
      response => Ok(response.body)
    }
  }

  def getContainersAll: Action[AnyContent] = servicesPermission.defaultHandler() {
    val request = ws.url(host + getContainersAllUrl)
    request.get().map {
      response => Ok(response.body)
    }
  }

  def getContainersInfo(id: String): Action[AnyContent] = servicesPermission.defaultHandler() {
    val request = ws.url(host + "/containers/" + id + "/json")
    request.get().map {
      response => Ok(response.body)
    }
  }

  def getImages: Action[AnyContent] = servicesPermission.defaultHandler() {
    val request = ws.url(host + getImagesUrl)
    request.get().map {
      response => Ok(response.body)
    }
  }

  def postContainerStop(id: String): Action[AnyContent] = servicesPermission.defaultHandler() {
    val request = ws.url(host + "/containers/" + id + "/stop")
    request.post("").map {
      response => Ok(response.body)
    }
  }

  def postContainerStart(id: String): Action[AnyContent] = servicesPermission.defaultHandler() {
    val request = ws.url(host + "/containers/" + id + "/start")
    request.post("").map {
      response => Ok(response.body)
    }
  }

  def createService: Action[JsValue] = servicesPermission.apply(parse.json) { request =>
    val bodyAsJson = request.body
    bodyAsJson.validate[ServiceScale] match {
      case _: JsSuccess[ServiceScale] => ws.url(host + "/services/create")
        .withHttpHeaders("Accept" -> "application/json").post(bodyAsJson).map {
        response =>
          //noinspection SimplifyBooleanMatch
          response.body contains "ID" match {
            case true => Ok(response.body)
            case false => Conflict("Service already exist")
          }
      }
      case JsError(error) => Future(BadRequest(JsError.toJson(error)))
    }
  }

  def updateService(id: String): Action[JsValue] = servicesPermission.apply(parse.json) { request =>
    ws.url(host + "/services/" + id)
      .get()
      .flatMap { response =>
        val version: String = Json.stringify((Json.parse(response.body) \ "Version" \ "Index").get)
        request.body.validate[ServiceScale] match {
          case _: JsSuccess[ServiceScale] => ws.url(host + "/services/" + id + "/update?version=" + version)
            .withHttpHeaders("Accept" -> "application/json").post(request.body).map {
            response =>
              Ok(response.body)
          }
          case JsError(error) => Future(BadRequest(JsError.toJson(error)))
        }
      }
  }

  def deleteService(id: String): Action[AnyContent] = servicesPermission.defaultHandler() {
    val deleteServiceURL = ws.url(host + "/services/" + id)
    deleteServiceURL.execute("DELETE").map {
      response => Ok(response.body)
    }
  }

  // TODO: cleanup
  def list: Action[AnyContent] = servicesPermission.defaultHandler() {

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
