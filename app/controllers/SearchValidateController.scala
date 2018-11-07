package controllers


import javax.inject.Inject

import actor.{UpdateValidateActor, SearchValidateActor}
import actor.SearchValidateActor.SearchValidate
import akka.actor.ActorSystem
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.ask

import scala.xml.{Elem, Node, NodeSeq}
import scala.concurrent._
import ExecutionContext.Implicits.global
import play.api.mvc._

/**
  * Created by momos_000 on 2017/9/4.
  */
class SearchValidateController @Inject()(cc: ControllerComponents, system: ActorSystem) extends AbstractController(cc) {

  implicit val timeout: Timeout = 5.seconds

  val searchValidateAct = system.actorOf(SearchValidateActor.props,"search-validate0")
  val createPaymentDescriptionActor = system.actorOf(UpdateValidateActor.props,"create-Payment-Description0")

  def searchVallidate(userid: String)  = Action.async{ implicit request =>
    (searchValidateAct ? SearchValidate(userid)).mapTo[NodeSeq].map{ validateNode => {
          val name = (validateNode\"name").text
          val validate = (validateNode\"endvalidate").text
          val descriptionNodes: Array[Node] = (validateNode\"description"\"item").toArray
          val descriptions: Array[String] = descriptionNodes.map(node => node.text)
          Ok(views.html.validateInfo(name,validate,descriptions))
        }
      }
  }


}
