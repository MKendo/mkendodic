package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import play.api.db.DBApi
import play.api.mvc._
import service.BookingService
import model.BookingDescription
import play.api.data.Form

class BookingController @Inject()(cc: MessagesControllerComponents, system: ActorSystem)(dbapi: DBApi)  extends MessagesAbstractController(cc) {
  import controllers.BookingDescriptionForm.{BookingData, bookingForm}

  def createBooking = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok("")
//    val errorFnction = { formWithErrors: Form[BookingData] =>
//      BadRequest(OK("预约出现错误:"))
//    }
//
//    val successFunction = { bookingData: BookingData =>
//      val booking = BookingDescription(bookingData.name, bookingData.mobile, bookingData.wxopenid,bookingData.placetimeCode,bookingData.classdate,bookingData.whereknowusCode,bookingData.commitdatetime)
//      createBookingImpl(booking)
//      Redirect(OK("预约成功"))
//    }
//
//    val formBookingResult = bookingForm.bindFromRequest()
//    formBookingResult.fold(errorFnction, successFunction)

  }

  /**
    * 预约信息提交到数据库
    * @param bookingDescription
    * @return
    */
  def createBookingImpl(bookingDescription: BookingDescription) = {
      val bookingService = new BookingService(dbapi,bookingDescription)
      bookingService.createBooking()
  }

}
