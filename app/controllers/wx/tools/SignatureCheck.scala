package controllers.wx.tools

import play.Logger

/**
  * Created by momos_000 on 2017/9/12.
  */
object SignatureCheck {
  def token = {"mkendojiaojianzhiai"}

  def doChecksg(signature: String, timestamp: String, nonce: String, echostr: String, token: String): Boolean ={
    val sortedArray = Array(token,timestamp,nonce).sorted
    val sortedString = sortedArray.mkString("")
    Logger.debug(s"sortedString = $sortedString")
    val hashcode = HashLib.getSha1(sortedString)
    Logger.debug(s"hashcode = $hashcode")

    if(signature.equals(hashcode)){
      Logger.debug("signature.equals(hashcode). 正确返回")
      return true
    }else {
      Logger.debug("错误返回")
      return false
    }
  }
}
