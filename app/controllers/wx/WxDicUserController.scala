package controllers.wx

import javax.inject.Inject
import model.User
import play.api.db.DBApi
import play.api.mvc.{AbstractController, ControllerComponents}
import service.{CommonUnit, DicUserService, UserService}

class WxDicUserController @Inject()(cc: ControllerComponents)(dbapi: DBApi) extends AbstractController(cc) {

  //根据jscode存下openid和unionid,用于suburi小程序
  def userInfoRegister(jsCode : String) = Action{
    implicit request => {
      println("userInfoRegister...")

      if(jsCode==null || jsCode.isEmpty){
        Ok("注注册的时候，提供的jscode不能为空")
      }

      val vxGetUserInfo = new WxGetUserInfo(cc)
      val userService = new DicUserService(dbapi)

      val wxUseridInfo = vxGetUserInfo.getDicUseridInfoFromWx(jsCode)//调用微信接口获取openid,unionid
      println("openId = " + wxUseridInfo.openId)

      try {
        val dbuser = userService.findByWxOpenid(wxUseridInfo.openId)

        //已存在的就只更新unionid，不存在的重新增加
        println("dbuser = " + dbuser)
        if (dbuser == null) {
          println("当前用户不存在，增加用户")
          Ok(userService.registerWxUserIdInfo(wxUseridInfo))
        } else {
          println("当前用户已存在。userId = " + dbuser.id)
          if(dbuser.wxUnionId.isEmpty) {
            if(wxUseridInfo.unionId==null || wxUseridInfo.unionId.isEmpty){//个别人获取不到unionid那么就不保存，直接返回
              println("该用户获取不到unionId 不需要保存, userId = " + dbuser.id)
              Ok("SUCCESS_" + dbuser.id)
            }else {
              println("需要更新unionId, userId = " + dbuser.id)
              Ok(userService.updateWxUnionId(dbuser.id, wxUseridInfo.unionId))
            }
          }else{
            println("不需要更新, userId = " + dbuser.id)
            Ok("SUCCESS_" + dbuser.id)
          }
        }
      }catch{
        case ex:Exception => {
          println("用户注册时发生错误：" + ex.getMessage + " " + ex.getCause)
          Ok("用户注册时发生错误：" + ex.getMessage + " " + ex.getCause)
        }
      }
    }
  }

  /**
    * 更新用户的信息信息：包括妮称，头像url
    * @return
    */
  def updateWxUserInfo() = Action{
    implicit request => {
      println("updateWxUserInfo...")
      val userid = request.body.asFormUrlEncoded.get("userid").head
      val wxname = request.body.asFormUrlEncoded.get("wxname").head
      val wximgurl = request.body.asFormUrlEncoded.get("wximgurl").head

      if(!CommonUnit.isIntByRegex(userid)) {
        println(s"更新用户的信息信息传入的userid不合法：$userid")
        Ok(s"更新用户的信息信息传入的userid不合法：$userid")
      }else{
        val userService = new DicUserService(dbapi)
        Ok(userService.updateSimpleWxUserInfo(userid.toInt, wxname, wximgurl))
      }
    }
  }


  /**
    * 登录确认
    * @return
    */
  def userLoginConfirm() = Action{
    implicit request => {
      println("userLoginConfirm...")
      val userid = request.body.asFormUrlEncoded.get("userid").head

      if(!CommonUnit.isIntByRegex(userid)){
        println(s"登录时（userLoginConfirm）传入的userid不合法：$userid")
        Ok(s"登录时（userLoginConfirm）传入的userid不合法：$userid")
      }else{
        val userService = new DicUserService(dbapi)
        val dbuser = userService.findByUserid(userid)
        //println("dbuser = " + dbuser)
        if(dbuser!=null && dbuser.id == userid.toInt) {
          Ok("SUCCESS")
        }else{
          Ok(s"确认失败，找不到该用户信息，userid=$userid")
        }
      }
    }
  }

}
