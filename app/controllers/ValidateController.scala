package controllers
import akka.actor.ActorSystem
import javax.inject.Inject
import model.ValidateDescription
import play.api.data._
import play.api.mvc._
import play.api.db.DBApi
import service.MemberService

class ValidateController @Inject()(cc: MessagesControllerComponents, system: ActorSystem)(dbapi: DBApi)  extends MessagesAbstractController(cc) {

  import ValidateDescriptionForm._

  private val postUrl = routes.ValidateController.createValidateDescription()

  def searchValidates(userid: String)  = {
    val memberService = new MemberService(dbapi, userid)
    Action { implicit request => {
      val name = memberService.findName();
      val endvalidate = memberService.findEndValidate()
      val descriptions  = memberService.findDescriptions()

      Ok(views.html.validateUpdate(userid, name, endvalidate, descriptions, validateForm, postUrl))
      }
    }
  }

  def createValidateDescription = Action { implicit request: MessagesRequest[AnyContent] =>
      val errorFnction = { formWithErrors: Form[ValidateData] =>
        BadRequest(views.html.validateUpdate("", "", "", Array.empty[String], formWithErrors, postUrl))
      }

      val successFunction = { validateData: ValidateData =>
        // This is the good case, where the form was successfully parsed as a Data object.
        val validate = ValidateDescription(userId = validateData.userId, endValidate = validateData.endValidate, description = validateData.description)
        //TODO 写入新的有效期信息
        Redirect(routes.ValidateController.searchValidates(validate.userId)).flashing("info" -> "续费成功!")
      }

      val formValidationResult = validateForm.bindFromRequest
      formValidationResult.fold(errorFnction, successFunction)
    }
}
