package actor

import actor.SearchDictionaryActor.SearchEntry
import akka.actor._
import play.api.Logger

import scala.xml.Elem
import scala.xml.Node
import scala.xml.NodeSeq


object SearchDictionaryActor{
  def props = Props[SearchDictionaryActor]
  case class SearchEntry(key: String)
}

/**
  * Created by momos_000 on 2017/7/31.
  */
class SearchDictionaryActor extends Actor {
  import SearchDictionaryActor._

  private val logger = Logger(getClass)

  def receive = {
    case SearchEntry(key: String) => {
      val dictionary = {
        scala.xml.XML.loadFile("app/data/kendodictionary.xml")
      }

      sender() ! <dictionary id="" name="剑道 和英辞典" author="全日本剑道连盟出版 - 明剑馆翻译开发">
        {(dictionary \\ "entry").filter(_.\\("key").text.contains(key))}
      </dictionary>

    }
  }
}
