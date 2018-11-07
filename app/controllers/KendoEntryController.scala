package controllers

import javax.inject.Inject

import actor.DailyWordActor
import actor.DailyWordActor.DailyWord
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.xml.NodeSeq

/**
  * Created by momos_000 on 2017/8/21.
  */
class KendoEntryController @Inject()(cc: ControllerComponents, system: ActorSystem) extends AbstractController(cc) {
  implicit val timeout: Timeout = 5.seconds

  val dailyWordActor = system.actorOf(DailyWordActor.props,"kendo-entry")

//  /**
//    * 返回的是xml格式
//    * @return
//    */
//  def search() = Action.async{ implicit request =>
//    (dailyWordActor ? DailyWord()).mapTo[NodeSeq].map { entries =>
//      Ok(entries)
//    }
//  }

  /**
    * 返回的是网页数据
    * @return
    */
  def show() = Action.async{ implicit request =>
    (dailyWordActor ? DailyWord()).mapTo[NodeSeq].map { entries =>
      {
        var cnKey = ""
        var cnDes = ""
        var jpKey = ""
        var jpDes = ""
        var enKey=""
        var enDes = ""

        cnKey = (entries\"entry"\"keys"\"key").filter(node => node.attribute("language").exists(language=>language.text.equals("chinese"))).text
        cnDes = (entries\"entry"\"descriptions"\"description").filter(node => node.attribute("language").exists(language=>language.text.equals("chinese"))).text

        jpKey = (entries\"entry"\"keys"\"key").filter(node => node.attribute("language").exists(language=>language.text.equals("japanese"))).text
        jpDes = (entries\"entry"\"descriptions"\"description").filter(node => node.attribute("language").exists(language=>language.text.equals("japanese"))).text

        enKey = (entries\"entry"\"keys"\"key").filter(node => node.attribute("language").exists(language=>language.text.equals("english"))).text
        enDes = (entries\"entry"\"descriptions"\"description").filter(node => node.attribute("language").exists(language=>language.text.equals("english"))).text

        Ok(views.html.kendoentry(cnKey, cnDes, jpKey, jpDes, enKey, enDes)(!cnKey.isEmpty)(null))
      }
    }
  }

}

