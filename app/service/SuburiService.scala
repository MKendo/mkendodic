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
      return s"获取用户素振总量时传入的userId不合法：$userId"
    }

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          val total:Option[Int] = SQL(s"select sum(amount) total from user_suburis where userid=$userId")
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
      return s"获取用户排名时传入的userId不合法：$userId"
    }

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          val userRankings:List[Int] = SQL("select userid,sum(amount) total from user_suburis group by userid order by total desc")
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

  def findRankingList(count:Int): List[Ranking] = {

    println("findRankingList_count="+count)
    db.withConnection {
      implicit c: java.sql.Connection =>
        val newsParser: RowParser[Ranking] = Macro.namedParser[Ranking]
        val sql = SQL("select 0 ranking, u.id userid,u.wxname name,u.wximgurl imgurl,us.commitdatetime updateDatetime,sum(amount) total " +
          "from user_suburis us left join users u on us.userid=u.id " +
          "group by userid having us.commitdatetime=max(us.commitdatetime) " +
          "order by total desc limit 3; ").on(
          "db_keyword" -> s"%$count%")

        val rankings: List[Ranking] = {
          try {
            sql.as(newsParser.*)
          }catch{
            case ex: ArrayIndexOutOfBoundsException => {
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
            newRankings.append(Ranking((i + 1), ranking.userId, ranking.name, ranking.imgUrl, ranking.total, ranking.updateDatetime))
          }
          return newRankings.toList
        }
    }

  }

}
