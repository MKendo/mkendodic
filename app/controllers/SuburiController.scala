package controllers

import javax.inject.Inject
import model.Ranking
import play.api.db.DBApi
import play.api.mvc.{AbstractController, ControllerComponents}
import service.{CommonUnit, SuburiService}
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
        println("获取指定用户排名时发生错误,传得的userId不能为空：" + userId)
        Ok("0")
      }

      val suburiService = new SuburiService(dbapi)
      var uuserId = ""
      if(userId.startsWith(":")){
        uuserId = userId.substring(1)
      }
      if(!CommonUnit.isIntByRegex(uuserId)){
        println("获取指定用户排名时发生错误,传得的userId不是整型数字：" + uuserId)
        Ok("0")
      }

      val result = suburiService.findRanking(uuserId.toInt)
      if(CommonUnit.isIntByRegex(result)){
        Ok(result)
      }else{
        println("获取指定用户排名时发生错误，返回的结果是：" + result)
        Ok("0")
      }
    }
  }

  def getRankingList(count: String) = Action {
    implicit request => {
      val suburiService = new SuburiService(dbapi)
      implicit val rankingWrites = new Writes[Ranking] {
        def writes(ranking: Ranking) = Json.obj(
          "ranking" -> ranking.ranking,
          "userId" -> ranking.uuserId,
          "name" -> ranking.name,
          "imgUrl" -> ranking.imgUrl,
          "total" -> ranking.total,
          "updateDatetime" -> ranking.updateDatetime,
          "zan" -> ranking.zan)
      }
      var limit = 0
      if(count.startsWith(":") && CommonUnit.isIntByRegex(count.substring(1))){
        limit = count.substring(1).toInt
      }else if(CommonUnit.isIntByRegex(count)){
        limit = count.toInt
      }else{
        throw new Exception(s"输入的参数不是数字：pagecount = $count")
      }

      val rankings = suburiService.findRankingList(limit, 0)
      println("rankings count = " + rankings.length)
      val rankingJson = Json.toJson(rankings)
      //println("rankingJson = " + rankingJson.toString())
      Ok(rankingJson)
    }
  }

  def getRankingListPageable(pagecount: String) = Action {
    implicit request => {
      println("getRankingListPageable........")
      val suburiService = new SuburiService(dbapi)
      implicit val rankingWrites = new Writes[Ranking] {
        def writes(ranking: Ranking) = Json.obj(
          "ranking" -> ranking.ranking,
          "userId" -> ranking.uuserId,
          "name" -> ranking.name,
          "imgUrl" -> ranking.imgUrl,
          "total" -> ranking.total,
          "updateDatetime" -> ranking.updateDatetime,
          "zan" -> ranking.zan)
      }

      val limit = 10;//每页10条
      var offset = 0
      if(pagecount.startsWith(":") && CommonUnit.isIntByRegex(pagecount.substring(1))){
        offset = (pagecount.substring(1).toInt-1)*10
      }else if(CommonUnit.isIntByRegex(pagecount)){
        offset = (pagecount.toInt-1)*10
      }else{
        throw new Exception(s"输入的参数不是数字：pagecount = $pagecount")
      }

      val rankings = suburiService.findRankingList(limit,offset)
      println("rankings count = " + rankings.length)
      val rankingJson = Json.toJson(rankings)
      //println("rankingJson = " + rankingJson.toString())
      Ok(rankingJson)
    }
  }

  /**
    * 点赞
    * @return
    */
  def zan() =Action{

    implicit request => {
      val userId = request.body.asFormUrlEncoded.get("userid").head //点赞的人
      val zanUserId = request.body.asFormUrlEncoded.get("zanuserid").head //被赞的人

      if(CommonUnit.isIntByRegex(userId) && CommonUnit.isIntByRegex(zanUserId)) {
        val suburiService = new SuburiService(dbapi)
        val result = suburiService.zan(userId.toInt,zanUserId.toInt)
        Ok(result)
      }else{
        Ok(s"点赞的时候传入的参数有误：userId=$userId zanUserId=$zanUserId")
      }
    }
  }

}
