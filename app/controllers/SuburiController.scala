package controllers

import javax.inject.Inject
import model.Ranking
import play.api.db.DBApi
import play.api.mvc.{AbstractController, ControllerComponents}
import service.SuburiService
import play.api.libs.json._

class SuburiController @Inject()(cc: ControllerComponents)(dbapi: DBApi) extends AbstractController(cc) {

  def postNumber() = Action{
    implicit request => {
      println("userInfoPost...")

      try {
        val t_userid = request.body.asFormUrlEncoded.get("userid").head.toInt
        val t_number = request.body.asFormUrlEncoded.get("number").head.toInt
      }catch{
        case ex: Exception => {
          Ok("提交素振数据时发生错误：" + ex.getMessage)
        }
      }

      val userid = request.body.asFormUrlEncoded.get("userid").head.toInt
      val number = request.body.asFormUrlEncoded.get("number").head.toInt

      val suburiService = new SuburiService(dbapi)

      Ok(suburiService.addSuburiRecord(userid, number))
    }
  }

  /**
    * 获取指定用户素振总数量
    * @param userId
    * @return
    */
  def getUserAmountTotal(userId:String) = Action {
    implicit request => {
      if(userId==null || userId.isEmpty || userId.startsWith(":") && userId.length==1){
        println("获取指定用户素振总数量时发生错误,传得的userId不能为空" + userId)
        Ok("0")
      }

      val suburiService = new SuburiService(dbapi)
      var result = ""
      if(userId.startsWith(":")){
        result = suburiService.findAmountTotal(userId.substring(1).toInt)
      }else{
        result = suburiService.findAmountTotal(userId.toInt)
      }

      try(result.toInt)
      catch{case ex:Exception => {
        println("获取指定用户素振总数量时发生错误：" + result + "|" + ex.getMessage)
        Ok("0")
      }}
      Ok(result)
    }
  }

  def getRanking(userId:String) = Action {
    implicit request => {
      if(userId==null || userId.isEmpty || userId.startsWith(":") && userId.length==1){
        println("获取指定用户排名时发生错误,传得的userId不能为空" + userId)
        Ok("0")
      }

      val suburiService = new SuburiService(dbapi)
      var result = ""
      if(userId.startsWith(":")){
        result = suburiService.findRanking(userId.substring(1).toInt)
      }else{
        result = suburiService.findRanking(userId.toInt)
      }

      try(result.toInt)
      catch{case ex:Exception => {
        println("获取指定用户排名时发生错误：" + result + "|" + ex.getMessage)
        Ok("0")
      }}
      Ok(result)
    }
  }

  def getRankingList(count: String) = Action {
    implicit request => {
      val suburiService = new SuburiService(dbapi)
      implicit val rankingWrites = new Writes[Ranking] {
        def writes(ranking: Ranking) = Json.obj(
          "ranking" -> ranking.ranking,
          "userId" -> ranking.userId,
          "name" -> ranking.name,
          "imgUrl" -> ranking.imgUrl,
          "total" -> ranking.total,
          "updateDatetime" -> ranking.updateDatetime)
      }
      if(count.startsWith(":")){
        val rankings = suburiService.findRankingList(count.substring(1).toInt)
        println("rankings count = " + rankings.length)
        val rankingJson = Json.toJson(rankings)
        println("rankingJson = " + rankingJson.toString())
        Ok(rankingJson)
      }else{
        val rankings = suburiService.findRankingList(count.toInt)
        println("rankings count = " + rankings.length)
        val rankingJson = Json.toJson(rankings)
        println("rankingJson = " + rankingJson.toString())
        Ok(rankingJson)
      }
    }
  }

}
