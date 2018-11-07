package actor

import actor.TranslateActor.Translate
import akka.actor.{Actor, Props}

/**
  * Created by yunsong on 2018/1/17.
  */

object TranslateActor{
  def props = Props[TranslateActor]
  case class Translate()
}

class TranslateActor extends Actor {
  def receive = {
    case Translate() => {
    }
  }
}
