
package controllers

import javax.inject._

import com.typesafe.config.ConfigFactory
import play.api.libs.ws._
import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext
import play.api.libs.json._

import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.JsonBodyWritables._

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


def createService = Action.async {
  request =>
  val createServiceURL = ws.url(host + "/services/create")
  val bodyString = request.body.asText.mkString
  val bodyJson = Json.parse(bodyString)
  createServiceURL.withHttpHeaders(  "Accept" -> "application/json").post(bodyJson).map { 
    response =>
    Ok(response.body)
  }
}


def updateService(id : String, version : String) = Action.async {
  request => 
  if ( version == "" ) BadRequest("malformed version");
  val updateURL = ws.url(host + "/services/"+id+"/update?version="+version)
  val bodyString = request.body.asText.mkString
  val bodyJson = Json.parse(bodyString)
  updateURL.withHttpHeaders(  "Accept" -> "application/json").post(bodyJson).map { 
    response =>
    Ok(response.body)
  }
}


def deleteService (id : String) = Action.async {
  request => 
  val deleteServiceURL = ws.url(host+"/services/"+id)
  deleteServiceURL.execute("DELETE").map {
    response => Ok(response.body)
  }
}

def getServiceInfo (id : String ) = Action.async {
  val request = ws.url(host+"/services/"+id)
  request.get().map {
    response => Ok(response.body)
  }
}

}