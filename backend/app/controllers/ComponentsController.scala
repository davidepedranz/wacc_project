
package controllers

import javax.inject._

import be.objectify.deadbolt.scala.ActionBuilders
import be.objectify.deadbolt.scala.models.PatternType
import com.typesafe.config.ConfigFactory
import models.{Permission, Service}
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext

@Singleton
class ComponentsController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, config: Configuration,
                                     actionBuilders: ActionBuilders, bodyParsers: PlayBodyParsers, ws: WSClient) extends AbstractController(cc) {

  private val host = ConfigFactory.load().getString("docker_host")
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
    request =>
      val createServiceURL = ws.url(host + "/services/create")
      createServiceURL.withHttpHeaders("Accept" -> "application/json").post(request.body).map {
        response =>
          Ok(response.body)
      }
  }


  def updateService(id: String) = Action.async(parse.json) { request =>
    ws.url(host + "/services/" + id)
      .get()
      .flatMap { response =>
        val version: String = Json.stringify((Json.parse(response.body) \ "Version" \ "Index").get)
        val updateURL = ws.url(host + "/services/" + id + "/update?version=" + version)
        updateURL.withHttpHeaders("Accept" -> "application/json").post(request.body).map {
          response =>
            Ok(response.body)
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

  def list: Action[AnyContent] = servicesReadPermission.defaultHandler() {
    ws.url(host + "/services")
      .get()
      .map(response => response.json.as[Seq[Service]])
      .map(services => {
        Logger.debug(services.toString)
        Ok(Json.toJson(services))
      })
  }
}