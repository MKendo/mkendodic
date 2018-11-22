package controllers

object ValidateDescriptionForm {
  import play.api.data.Forms._
  import play.api.data.Form

  case class ValidateData(userId: String, endValidate: String, description: String)

    val validateForm = Form(
      mapping(
        "userId" -> nonEmptyText,
        "endValidate" -> nonEmptyText,
        "description" -> text
      )(ValidateData.apply)(ValidateData.unapply)
    )
}
