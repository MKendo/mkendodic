package actor

import actor.DailyWordActor.DailyWord
import actor.SearchDictionaryActor.SearchEntry
import akka.actor.{Actor, Props}

/**
  * Created by momos_000 on 2017/8/21.
  */

object DailyWordActor {
  def props = Props[DailyWordActor]
  case class DailyWord()
}

class DailyWordActor extends Actor {
    def receive = {
    case DailyWord() => {
      val dictionary = {
        scala.xml.XML.loadFile("app/data/kendodictionary.xml")
      }

      sender() ! <dictionary id="" name="剑道 和英辞典" author="全日本剑道连盟出版 - 明剑馆翻译开发">
        {(dictionary \\ "entry") (scala.util.Random.nextInt((dictionary \\ "entry").length ))}
        </dictionary>
    }
  }
}
