package controllers
import javax.inject.Inject
import akka.actor.ActorSystem
import play.api.db.DBApi
import play.api.mvc._
import service.MemberService

/**
  * Created by momos_000 on 2017/9/4.
  */
class SearchValidateController @Inject()(cc: ControllerComponents, system: ActorSystem)(dbapi: DBApi) extends AbstractController(cc) {

  def searchValidate(userid: String) = {
    val memberService = new MemberService(dbapi, userid)
    Action {
      val name = memberService.findName();
      val endvalidate = memberService.findEndValidate()
      val descriptions  = memberService.findDescriptions()
      if(name.isEmpty){
        Ok("会员名册中没找到 "+userid+" 的信息，如果您是明剑馆会员请联系西瓜。")
      }else {
        Ok(userid + "您的有效期至：" + endvalidate)
      }
    }
  }

   def searchValidateAll(userid: String) = {
    val memberService = new MemberService(dbapi, userid)
    Action {
      val name = memberService.findName();
      val endvalidate = memberService.findEndValidate()
      val descriptions  = memberService.findDescriptions()
      if(name.isEmpty){
        Ok(views.html.messageInfo("会员名册中没找到您的信息，如果您是明剑馆会员请联系西瓜师姐。"))
      }else {
        Ok(views.html.validateInfo(name, endvalidate, descriptions))
      }
    }
  }

}
