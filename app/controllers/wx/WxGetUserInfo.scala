package controllers.wx

import javax.inject.Inject
import play.mvc.Http._
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._


class WxGetUserInfo @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val APPID : String = "wxe6a6249f535bcf1c"
  val SECRET : String = "1690519b355a6c59c66687084359ea2c"

  def getOpenid(jsCode : String) =Action{
    Ok(getOpenidFromWx(jsCode))
  }

  def getMemberName(jsCode: String) =Action{
    //获取openid
    //数据库有的话，获取姓名返回
    //数据库没有的话，增加一条，返回空的姓名？？
    Ok("")
  }

  def getOpenidFromWx(jsCode : String): String ={
    println("getOpenidFromWx start...")
    var rjscode=""
    if(jsCode.startsWith(":")){
      rjscode = jsCode.substring(1)
    }
    val code2Session_url = s"https://api.weixin.qq.com/sns/jscode2session?appid=$APPID&secret=$SECRET&js_code=$rjscode&grant_type=authorization_code"
    println("code2Session_url = " + code2Session_url)
    val reponseJson = scala.io.Source.fromURL(code2Session_url).mkString
    println("reponseJson = " + reponseJson)
    val jsonValue = Json.parse(reponseJson)
    println("jsonValue = " + jsonValue)
    return (jsonValue \\ "openid").mkString
  }
}
