package controllers.wx

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents}

/**
  * Created by momos_000 on 2017/9/6.
  */
class checkSignatureController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def checksg = Action { implicit request =>
    Ok("MnzqtpFyBPkneHZXgG3YmqKeQz7P7QmwL94etVQTx9w")
//  Ok(request.queryString("echostr").toString())
  }

}
