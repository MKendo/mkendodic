package controllers.wx

import javax.inject.Inject

import play.Logger
import play.api.mvc.{AbstractController, ControllerComponents}

/**
  * Created by momos_000 on 2017/9/6.
  */
class CheckSignatureController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def checksg = Action {
    implicit request =>
      val signature = request.queryString("signature").mkString
      val timestamp = request.queryString("timestamp").mkString
      val nonce = request.queryString("nonce").mkString
      val echostr = request.queryString("echostr").mkString
      val token = "mkendojiaojianzhiai"

      Logger.debug(s"signature = $signature")
      Logger.debug(s"timestamp = $timestamp")
      Logger.debug(s"nonce = $nonce")
      Logger.debug(s"echostr = $echostr" )

      val sortedArray = Array(token,timestamp,nonce).sorted
      val sortedString = sortedArray.mkString("")
      Logger.debug(s"sortedString = $sortedString")
      val hashcode = HashLib.getSha1(sortedString)
      Logger.debug(s"hashcode = $hashcode")

      if(signature.equals(hashcode)){
        Ok(echostr+"")
      }
      Ok("")
  }

//  object SHA1
//  {
//    def digest_bytes(bytes: Array[Byte]): String =
//    {
//      val result = new StringBuilder
//      for (b <- MessageDigest.getInstance("SHA").digest(bytes)) {
//        val i = b.asInstanceOf[Int] & 0xFF
//        if (i < 16) result += '0'
//        result ++= Integer.toHexString(i)
//      }
//      result.toString
//    }
//
//    def digest(s: String): String = digest_bytes(Standard_System.string_bytes(s))
//  }

}
