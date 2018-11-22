package controllers

import javax.inject.Inject
import model.ValidateDescription
import play.api.data._
import play.api.mvc._


class ValidateController @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {

  import ValidateDescriptionForm._

  private val validates = scala.collection.mutable.ArrayBuffer(
    ValidateDescription("huangyunsong", "2017/11/9", "新人报名年费"),
    ValidateDescription("huangyunsong", "2018/11/9", "续费")
  )

  private val postUrl = routes.ValidateController.createValidateDescription()

  def searchValidates = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.validateUpdate(validates, validateForm, postUrl))
  }

  def createValidateDescription = Action { implicit request: MessagesRequest[AnyContent] =>
      val errorFnction = { formWithErrors: Form[ValidateData] =>
        BadRequest(views.html.validateUpdate(validates, formWithErrors, postUrl))
      }

      val successFunction = { validateData: ValidateData =>
        // This is the good case, where the form was successfully parsed as a Data object.
        val validate = ValidateDescription(userId = validateData.userId, endValidate = validateData.endValidate, description = validateData.description)
        validates.append(validate)
        Redirect(routes.ValidateController.searchValidates()).flashing("info" -> "续费成功!")
      }

      val formValidationResult = validateForm.bindFromRequest
      formValidationResult.fold(errorFnction, successFunction)
    }
}
