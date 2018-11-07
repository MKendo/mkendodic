package controllers

import javax.inject.Inject

import actor.{SearchValidateActor, UpdateValidateActor}
import actor.SearchValidateActor.SearchValidate
import actor.UpdateValidateActor.UpdateValidate
import akka.actor.ActorSystem
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.ask
import play.api.Logger

import scala.xml.{Elem, Node, NodeSeq}
import scala.concurrent._
import ExecutionContext.Implicits.global
import play.api.mvc._

import scala.xml.{Node, NodeSeq}

/**
  * Created by yunsong on 2018/3/1.
  */
class UpdateValidateController @Inject()(cc: ControllerComponents, system: ActorSystem) extends AbstractController(cc) {

  implicit val timeout: Timeout = 5.seconds

  val searchValidateAct = system.actorOf(SearchValidateActor.props,"search-validate1")
  val updateValidateActor = system.actorOf(UpdateValidateActor.props,"update-validate1")

  def searchVallidate(userid: String)  = {
    Action.async { implicit request =>
      (searchValidateAct ? SearchValidate(userid)).mapTo[NodeSeq].map { validateNode => {
        val name = (validateNode \ "name").text
        val validate = (validateNode \ "endvalidate").text
        val descriptionNodes: Array[Node] = (validateNode \ "description" \ "item").toArray
        val descriptions: Array[String] = descriptionNodes.map(node => node.text)
        Ok(views.html.validateInfoUpdate(name, validate, descriptions))
      }
      }
    }

  }

  def searchVallidate()  = {
    Action.async { implicit request =>
      val userid = {
        if(request.getQueryString("userid").isEmpty)
          "N"
        else
          request.getQueryString("userid").get
      }

      (searchValidateAct ? SearchValidate(userid)).mapTo[NodeSeq].map { validateNode => {
        val name = (validateNode \ "name").text
        val validate = (validateNode \ "endvalidate").text
        val descriptionNodes: Array[Node] = (validateNode \ "description" \ "item").toArray
        val descriptions: Array[String] = descriptionNodes.map(node => node.text)
        Ok(views.html.validateInfoUpdate(name, validate, descriptions))
      }
      }
    }
  }

  def updateValidate(validate: String, description: String): Unit ={
    Action.async { implicit request =>
      val userid = {
        if(request.getQueryString("userid").isEmpty)
          "N"
        else
          request.getQueryString("userid").get
      }

      val endValidate = request.getQueryString("validate").get
      val description = request.getQueryString("des").get

      (updateValidateActor ? UpdateValidate(userid, endValidate, description)).mapTo[NodeSeq].map { validateNode => {
        val newName = (validateNode \ "name").text
        val newValidate = (validateNode \ "endvalidate").text
        val newDescriptionNodes: Array[Node] = (validateNode \ "description" \ "item").toArray
        val newDescriptions: Array[String] = newDescriptionNodes.map(node => node.text)
        Ok(views.html.validateInfoUpdate(newName, validate, newDescriptions))
      }
      }
    }
  }



}
