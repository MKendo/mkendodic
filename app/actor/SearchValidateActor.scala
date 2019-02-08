package actor

import actor.SearchValidateActor.SearchValidate
import akka.actor.{Actor, Props}
import play.api.Logger

import scala.xml.NodeSeq


/**
  * Created by momos_000 on 2017/9/4.
  */

object SearchValidateActor {
  def props = Props[SearchValidateActor]
  case class SearchValidate(userid: String)
}

 class SearchValidateActor extends Actor {
   private val logger = Logger(getClass)

   def receive = {
     case SearchValidate(userid: String) => {
       val validateXml = {
         scala.xml.XML.loadFile("app/data/validate.xml")
       }

       val validateNode: NodeSeq = (validateXml \\ "validate").filter(_.\\("userid").text.equals(userid))
       logger.info("validateNode = "+validateNode.toString())
       sender() ! validateNode
     }
   }
 }
