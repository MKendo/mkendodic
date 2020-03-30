package service

import java.text.SimpleDateFormat
import java.util.Date

import anorm.{Macro, RowParser, SQL, SqlParser}
import javax.inject.Inject
import model.{Ranking, User}
import play.api.db.DBApi

import scala.collection.mutable.ListBuffer

class SuburiService @Inject()(dbapi: DBApi) {

  private val db = dbapi.database("default")

  def addSuburiRecord(userId: Int, number: Int):String = {

    println("addSuburiRecord_userId = " + userId)
    println("addSuburiRecord_number = " + number)

    if(userId <= 0){
      return s"增加素振记录时，传入的userId（$userId）不合法"
    }

    if(number <= 0){
      return s"增加素振记录时，传入的number（$number）不合法"
    }

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          SQL("insert into user_suburis(userid,amount,commitdatetime,description,enable) " +
            "values({db_userId} ,{db_amount},{db_commitdatetime},{db_description},1)")
            .on("db_userId" -> userId,
            "db_amount" -> number,
            "db_commitdatetime" -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date),
            "db_description" -> ""
          ).executeInsert()
      }
      return "SUCCESS"
    }catch
      {
        case ex:Exception => {
          return "写入数据库时出错：" + ex.getMessage
        }
      }
    return "未知问题，请联系管理员或重试一次。";

  }

  def findAmountTotal(userId: Int): String ={
    if(userId <= 0){
      println(s"获取用户素振总量时传入的userId不合法：$userId")
      return "0"
    }

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          val total:Option[Int] = SQL(s"select ifnull(sum(amount),0) total from user_suburis where userid=$userId")
            .as(SqlParser.int("total").singleOpt)
          return total.getOrElse(0).toString
      }
    }catch{
      case ex:Exception =>{
        val error = s"计算用户 $userId 的素振总量时出现错误：" + ex.getMessage
        println(error)
        return "0"
      }
    }
  }

  def findRanking(userId:Int) : String = {
    if(userId <= 0){
      println(s"获取用户排名时传入的userId不合法：$userId")
      return "0"
    }

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          val userRankings:List[Int] = SQL("select userid,ifnull(sum(amount),0) total from user_suburis group by userid order by total desc")
            .as(SqlParser.int("userid").*)
          return (userRankings.indexOf(userId)+1).toString
      }
    }catch{
      case ex:Exception =>{
        val error = s"计算用户 $userId 的排名时出现错误：" + ex.getMessage
        println(error)
        return "0"
      }
    }
  }

//  def findRankingList(count:Int): List[Ranking] = {
//
//    println("findRankingList_count="+count)
//    db.withConnection {
//      implicit c: java.sql.Connection =>
//        val newsParser: RowParser[Ranking] = Macro.namedParser[Ranking]
//        val sql = SQL(
//          "select ranking,uuserid,name,imgurl,updateDatetime,total,ifnull(sum(uz.count),0) zan from (" +
//          "select 0 ranking, u.id uuserid,u.wxname name,u.wximgurl imgurl,us.commitdatetime updateDatetime,ifnull(sum(amount),0) total " +
//          "from users u left join user_suburis us on u.id = us.userid " +
//          "group by u.id having us.commitdatetime=max(us.commitdatetime)) " +
//            //"order by total desc limit {db_count}) " +
//            "left join user_zans uz on uz.zanuserid = uuserid " +
//            "group by uuserid order by total desc limit {db_count}; ").on(
//          "db_count" -> count)
//
//        val rankings: List[Ranking] = {
//          try {
//            sql.as(newsParser.*)
//          }catch{
//            case ex: ArrayIndexOutOfBoundsException => {
//              println("findRankingList出现错误" + ex.getMessage)
//              return Nil;
//            }
//          }
//        }
//        println("findRankingList_rankings length = " + rankings.length)
//
//        if(rankings.length==0){
//          return Nil
//        }else {
//          //加入名次
//          val newRankings: ListBuffer[Ranking] = ListBuffer[Ranking]()
//          for (i <- rankings.indices) {
//            val ranking = rankings(i)
//            newRankings.append(Ranking((i + 1), ranking.uuserId, ranking.name, ranking.imgUrl, ranking.total, ranking.updateDatetime,ranking.zan))
//          }
//          return newRankings.toList
//        }
//    }
//
//  }

  /**
    * 分页 跳过offset行，取limit行。例如：跳过0行取10行，是第1页，跳过10行取10行是第2页，跳过20行取10行是第3页
    * @param limit 要输出的行数
    * @param offset 偏离量
    * @return
    */
  def findRankingList(limit:Int, offset:Int): List[Ranking] ={
    println(s"findRankingList limit=$limit  offset=$offset")
    db.withConnection {
      implicit c: java.sql.Connection =>
        val newsParser: RowParser[Ranking] = Macro.namedParser[Ranking]
        val sql = SQL(
          "select ranking,uuserid,name,imgurl,updateDatetime,total,ifnull(sum(uz.count),0) zan from (" +
            "select 0 ranking, u.id uuserid,u.wxname name,u.wximgurl imgurl,us.commitdatetime updateDatetime,ifnull(sum(amount),0) total " +
            "from users u left join user_suburis us on u.id = us.userid " +
            "group by u.id having us.commitdatetime=max(us.commitdatetime)) " +
            "left join user_zans uz on uz.zanuserid = uuserid " +
            "group by uuserid order by total desc limit {db_limit} offset {db_offset}; ").on(
          "db_limit" -> limit,
            "db_offset" -> offset)

        val rankings: List[Ranking] = {
          try {
            sql.as(newsParser.*)
          }catch{
            case ex: Exception => {
              println("findRankingList出现错误" + ex.getMessage)
              return Nil;
            }
          }
        }
        println("findRankingList_rankings length = " + rankings.length)

        if(rankings.length==0){
          return Nil
        }else {
          //加入名次
          val newRankings: ListBuffer[Ranking] = ListBuffer[Ranking]()
          for (i <- rankings.indices) {
            val ranking = rankings(i)
            newRankings.append(Ranking((i + 1 + offset), ranking.uuserId, ranking.name, ranking.imgUrl, ranking.total, ranking.updateDatetime,ranking.zan))
          }
          return newRankings.toList
        }
    }
  }

  /**
    * 给用户点赞
    * @param userId 点赞的用户
    * @param zanUserId 被赞的用户
    */
  def zan(userId: Int, zanUserId: Int): String ={
    if(userId <=0 || zanUserId <=0){
      return s"传入的点赞用户ID非法：userId=$userId zanUserId=$zanUserId"
    }

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          SQL("insert into user_zans(userid,zanuserid,count,commitdatetime,enable) " +
            "values({db_userid},{db_zanuserid},1,{db_commitdatetime},1)")
            .on("db_userid" -> userId,
              "db_zanuserid" -> zanUserId,
              "db_commitdatetime" -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date)
            ).executeInsert()
      }
      return "SUCCESS"
    }catch
      {
        case ex:Exception => {
          return "给用户点赞时出错：" + ex.getMessage
        }
      }
    return "未知问题，请联系管理员或重试一次。";
  }

}
