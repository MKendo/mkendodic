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
import scala.io.Source

/**
  * Created by momos_000 on 2017/9/7.
  */
class WxSearchValidateController @Inject()(cc: ControllerComponents, system: ActorSystem)  extends AbstractController(cc) {

  implicit val timeout: Timeout = 5.seconds

  val searchValidateAct = system.actorOf(SearchValidateActor.props,"search-validate")

  def searchVallidate()  = Action.async{ implicit request =>
    Logger.debug("re8888888888888888")
    val message = request.queryString.mkString("")
    Logger.debug("request.uri = " + request.uri)

    val openid = request.queryString("openid").mkString
    Logger.debug(s"message = $message")
    Logger.debug(s"openid = $openid")

    val userid = "huhoucun"
    (searchValidateAct ? SearchValidate(userid)).mapTo[String].map{ endvalidate =>
      Ok(<xml>
        <ToUserName>{"<![CDATA[" + openid + "]]>"}</ToUserName>
        <FromUserName>{"<![CDATA[xigua291162]]>"}</FromUserName>
        <CreateTime>{"20170912"}</CreateTime>
        <MsgType>{"<![CDATA[text]]>"}</MsgType>
        <Content>{"<![CDATA[" + endvalidate + "]]>"}</Content>
      </xml>)
    }
  }

}
