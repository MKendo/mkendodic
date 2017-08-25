package controllers

import javax.inject.Inject

import actor.SearchDictionaryActor
import actor.SearchDictionaryActor.SearchEntry
import akka.actor.ActorSystem

import play.api.mvc._
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask
import scala.xml.Elem
import scala.xml.NodeSeq
import scala.concurrent._
import ExecutionContext.Implicits.global

/**
  * Created by momos_000 on 2017/7/31.
  */
class SearchDirctionaryControlller  @Inject()(cc: ControllerComponents, system: ActorSystem) extends AbstractController(cc) {

  implicit val timeout: Timeout = 5.seconds

  val searchDirctionary = system.actorOf(SearchDictionaryActor.props,"search-dictionary")

  def search(key: String) = Action.async{ implicit request =>
    (searchDirctionary ? SearchEntry(key)).mapTo[NodeSeq].map { entries =>
      Ok(entries)
    }
  }

}
