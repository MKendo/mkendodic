package controllers

import actor.SearchValidateActor
import actor.SearchValidateActor.SearchValidate
import akka.actor.ActorSystem
import scala.util.{Failure, Success}
import akka.util.Timeout
import javax.inject.Inject
import model.ValidateDescription
import play.api.data._

import scala.xml.{Node, NodeSeq}
import scala.concurrent._
import ExecutionContext.Implicits.global
import play.api.mvc._

import scala.concurrent.duration._
import akka.pattern.ask


class ValidateController @Inject()(cc: MessagesControllerComponents, system: ActorSystem) extends MessagesAbstractController(cc) {

  import ValidateDescriptionForm._

  private val postUrl = routes.ValidateController.createValidateDescription()
  implicit val timeout: Timeout = 5.seconds
  val searchValidateAct = system.actorOf(SearchValidateActor.props,"search-validate2")

  def searchValidates(userid: String)  = {
    Action.async { implicit request =>
      (searchValidateAct ? SearchValidate(userid)).mapTo[NodeSeq].map { validateNode => {
        val name = (validateNode \ "name").text
        val endvalidate = (validateNode \ "endvalidate").text
        val descriptionNodes: Array[Node] = (validateNode \ "description" \ "item").toArray
        val validates = descriptionNodes.map(desnode => ValidateDescription(userid, "", desnode.text))
        println("提交续费业务 即将成功：" + name + " " + endvalidate)
        Ok(views.html.validateUpdate(userid, name, endvalidate, validates,validateForm, postUrl))
      }
      }
    }
  }

  /*
  def searchValidates(userid: String) =  {
    (searchValidateAct ? SearchValidate(userid)).mapTo[NodeSeq].onComplete {
      case Success(validateNode) => {
        println("提交续费业务 开始")
        val name = (validateNode \ "name").text
        val endvalidate = (validateNode \ "endvalidate").text
        val descriptionNodes: Array[Node] = (validateNode \ "description" \ "item").toArray
        val validates = descriptionNodes.map(desnode => ValidateDescription(userid, "", desnode.text))
        println("提交续费业务 即将成功：" + name + " " + endvalidate)
        Action { implicit request: MessagesRequest[AnyContent] =>
          Ok(views.html.validateUpdate(userid, name, endvalidate, validates, validateForm, postUrl))
        }
      }
      case Failure(e) => {
        println("提交续费业务 出错：" + e.printStackTrace)
        Action { implicit request: MessagesRequest[AnyContent] =>
          Ok(views.html.validateUpdate("", "", "", Nil, validateForm, postUrl))
        }
      }
    }
    //TODO 必须有返回 考虑 Action改为异步
  }
  */


/*
  def searchValidates(userid: String) = Action {implicit request: MessagesRequest[AnyContent] =>
    (searchValidateAct ? SearchValidate(userid)).mapTo[NodeSeq].map { validateNode => {
        name = (validateNode \ "name").text
        endvalidate = (validateNode \ "endvalidate").text
        val descriptionNodes: Array[Node] = (validateNode \ "description" \ "item").toArray
        validates = descriptionNodes.map(desnode => ValidateDescription(userid, "", desnode.text))
     }
    }
    Ok(views.html.validateUpdate(userid, name, endvalidate, validates, validateForm, postUrl))
  }*/

  def createValidateDescription = Action { implicit request: MessagesRequest[AnyContent] =>
      val errorFnction = { formWithErrors: Form[ValidateData] =>
        BadRequest(views.html.validateUpdate("", "", "", Nil, formWithErrors, postUrl))
      }

      val successFunction = { validateData: ValidateData =>
        // This is the good case, where the form was successfully parsed as a Data object.
        val validate = ValidateDescription(userId = validateData.userId, endValidate = validateData.endValidate, description = validateData.description)
        //TODO 写入新的有效期信息
        Redirect(routes.ValidateController.searchValidates(validate.userId)).flashing("info" -> "续费成功!")
      }

      val formValidationResult = validateForm.bindFromRequest
      formValidationResult.fold(errorFnction, successFunction)
    }
}
