package controllers.wx

import javax.inject.Inject

import controllers.wx.tools.{HashLib, SignatureCheck}
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

      Logger.debug(s"signature = $signature")
      Logger.debug(s"timestamp = $timestamp")
      Logger.debug(s"nonce = $nonce")
      Logger.debug(s"echostr = $echostr" )

      if(SignatureCheck.doChecksg(signature, timestamp, nonce, echostr, SignatureCheck.token)) {
        Ok(echostr + "")
      }else{
        Ok("")
      }

//      val sortedArray = Array(token,timestamp,nonce).sorted
//      val sortedString = sortedArray.mkString("")
//      Logger.debug(s"sortedString = $sortedString")
//      val hashcode = HashLib.getSha1(sortedString)
//      Logger.debug(s"hashcode = $hashcode")
//
//      if(signature.equals(hashcode)){
//        Logger.debug("signature.equals(hashcode). 正确返回")
//        Ok(echostr+"")
//      }else {
//        Logger.debug("错误返回")
//        Ok("")
//      }
  }



}
