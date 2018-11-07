package actor

import actor.UpdateValidateActor.{UpdateValidate, getClass}
import actor.SearchValidateActor.SearchValidate
import akka.actor.{Actor, Props}
import play.api.Logger

import scala.xml.NodeSeq

/**
  * Created by yunsong on 2018/2/28.
  */

object UpdateValidateActor {
  def props = Props[SearchValidateActor]
  case class UpdateValidate(userid: String, endValidate: String, description: String)
}

class UpdateValidateActor extends Actor {
  private val logger = Logger(getClass)
  def receive = {
    case UpdateValidate(userid: String, endValidate: String, description: String) => {
      val validateXml = {
        scala.xml.XML.loadFile("app/data/validate.xml")
      }

      val validateNode: NodeSeq = (validateXml \\ "validate").filter(_.\\("userid").text.equals(userid))
      logger.info("validateNode = "+validateNode.toString())
      //TODO: XML不能修改先结构化再进行修改后save

      sender() ! validateNode //TODO:这里返回最新的节点信息
    }
  }
}
