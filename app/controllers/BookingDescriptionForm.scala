package controllers

import akka.http.scaladsl.model.DateTime

object BookingDescriptionForm {
  import play.api.data.Forms._
  import play.api.data.Form
  //import model.BookingDescription

  case class BookingData(name: String, mobile: String, wxopenid: String, placetimeCode: String, classdate: String, whereknowusCode: String, commitdatetime: String)

  val bookingForm = Form(
  mapping(
    "name" -> nonEmptyText,
    "mobile" -> nonEmptyText,
    "wxopenid" -> text,//TODO
    "placetimeCode" -> text,//TODO
    "classdate" -> text,//TODO
    "whereknowusCode" -> text,//TODO
    "commitdatetime" -> text//TODO
    )(BookingData.apply)(BookingData.unapply)
  )

}
