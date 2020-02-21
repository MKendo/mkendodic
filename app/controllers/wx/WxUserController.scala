package controllers.wx

import javax.inject.Inject
import model.User
import play.api.db.DBApi
import play.api.mvc.{AbstractController, ControllerComponents}
import service.UserService

class WxUserController @Inject()(cc: ControllerComponents)(dbapi: DBApi) extends AbstractController(cc) {

  def userInfoPost() = Action{

    implicit request => {
      println("userInfoPost...")

      val wxopenId = request.body.asFormUrlEncoded.get("wxopenid").head
      val wxname = request.body.asFormUrlEncoded.get("wxname").head
      val wximgUrl = request.body.asFormUrlEncoded.get("wximgurl").head

      println("wxopenId = " + wxopenId)
      println("wxname = " + wxname)
      println("wximgUrl = " + wximgUrl)

      println(s"userInfoPost_wxopenId = $wxopenId")
      println("length = " + wxopenId.length)
      if(wxopenId==null || wxopenId.equals("null")){
        Ok("wxopenId为空")
      }else{
        val user = User(-1,"","","",wxopenId,wxname,wximgUrl,"")
        val userService = new UserService(dbapi)

        val dbuser = userService.findByWxOpenid(wxopenId)
        if(dbuser == null){
          println("addUser")
          Ok(userService.addUser(user))
        }else{
          println(s"updateWxUser $dbuser.id")
          Ok(userService.updateWxUser(dbuser.id,user))
        }
      }
    }
  }

}
