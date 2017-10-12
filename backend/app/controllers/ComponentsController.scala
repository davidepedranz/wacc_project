
package controllers

import javax.inject._

import com.typesafe.config.ConfigFactory
import play.api.libs.ws._
import play.api.mvc._

import scala.concurrent.ExecutionContext


@Singleton
class ComponentsController @Inject()(implicit ec: ExecutionContext, ws: WSClient,cc: ControllerComponents ) extends AbstractController(cc) {

  val host = ConfigFactory.load().getString("docker_host")
  val getContainersUrl = "/containers/json"
  val getContainersAllUrl = "/containers/json?all=true"
  val getImagesUrl = "/images/json"


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


  //  def createContainer = Action() {
  //  request => 
  //  val json_par = request.body
  //val createContainer = "curl -vX POST "+host+"containers/create -d " !!;
  //  Redirect("/containers")
  // }


}
