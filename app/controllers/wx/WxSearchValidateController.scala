package controllers.wx

import javax.inject.Inject

import actor.SearchDictionaryActor.SearchEntry
import actor.SearchValidateActor
import actor.SearchValidateActor.SearchValidate
import akka.actor.ActorSystem
import play.api.mvc._
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.ask
import play.Logger

import scala.xml.Elem
import scala.xml.NodeSeq
import scala.concurrent._
import ExecutionContext.Implicits.global

/**
  * Created by momos_000 on 2017/9/7.
  */
class WxSearchValidateController @Inject()(cc: ControllerComponents, system: ActorSystem) extends AbstractController(cc) {

  implicit val timeout: Timeout = 5.seconds

  val searchValidateAct = system.actorOf(SearchValidateActor.props,"search-validate")

  def searchVallidate()  = Action.async{ implicit request =>
    val message = request.queryString.mkString("")
    Logger.debug(s"message = $message")

    val userid = "huhoucun"
    (searchValidateAct ? SearchValidate(userid)).mapTo[String].map{ endvalidate =>
      Ok(endvalidate)
    }
  }
}
