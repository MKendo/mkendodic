package service

import java.text.SimpleDateFormat
import java.util.Date

import anorm.{Macro, RowParser, SQL, SqlParser}
import javax.inject.Inject
import model.{Comment}
import play.api.db.DBApi

class DicCommentService  @Inject()(dbapi: DBApi) {

  private val db = dbapi.database("default")


  def findById(id:Int): Comment = {
    println(s"findByUserid_userid = $id")
    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          val newsParser: RowParser[Comment] = Macro.namedParser[Comment]
          return SQL("select *,c.commitdatetime commitdatetime, u.commitdatetime ucommitdatetime, u.wxname dicUserWxname, u.wximgurl dicUserWximgurl " +
            "from comments c " +
            "left join dicusers u on c.dicuserid = u.id " +
            "where c.enable=1 and c.id = {db_id} ")
            .on("db_id" -> id)
            .as(newsParser.single)
      }
    }catch{
      case ex:Exception =>{
        println("findById()=null" + ex.getMessage)
        return null;
      }
    }
  }

  def searchCommentsByEntryCode(entryCode:String): List[Comment] ={
    db.withConnection {
      implicit c: java.sql.Connection =>
        val newsParser: RowParser[Comment] = Macro.namedParser[Comment]

        val result: List[Comment] = SQL(
          "select *,c.id id,u.id uid,c.commitdatetime commitdatetime, u.commitdatetime ucommitdatetime, u.wxname dicUserWxname, u.wximgurl dicUserWximgurl " +
            "from comments c " +
            "left join dicusers u on c.dicuserid = u.id " +
            "where c.enable=1 and forentrycode = {db_entrycode} " +
            "order by c.commitdatetime desc").on(
          "db_entrycode" -> entryCode).as(newsParser.*)
        return result
    }
    return Nil;
  }

  def searchNewComments(count: Int): List[Comment] ={
    db.withConnection {
      implicit c: java.sql.Connection =>
        val newsParser: RowParser[Comment] = Macro.namedParser[Comment]

        val result: List[Comment] = SQL(
          "select *, c.commitdatetime commitdatetime, u.commitdatetime ucommitdatetime,u.wxname dicUserWxname, u.wximgurl dicUserWximgurl " +
            "from comments c " +
            "left join dicusers u on c.dicuserid = u.id " +
            "where c.enable=1 " +
            "order by c.commitdatetime desc limit {db_count}").on(
          "db_count" -> count).as(newsParser.*)
        return result
    }
    return Nil;
  }

  def createComment(comment: Comment): Int ={
    println("createComment.......")
    var entryId = -1

    try {
      //获取entryid
      db.withConnection {
        implicit c: java.sql.Connection =>
          val id: Option[Int] = SQL("select id from dicentries where code={db_entryCode} and language='JA' and enable=1").on(
            "db_entryCode" -> comment.forEntryCode).as(
            SqlParser.int("dicentries.id").singleOpt)
          entryId = Integer.valueOf(id.get)
      }

      println("entryId = " + entryId.toString)

      db.withConnection {
        implicit c: java.sql.Connection =>
          val commitDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date)
          println("commitDatetime = " + commitDatetime)
          val result: Option[Long] = SQL("insert into " +
            "comments(forentryid,forentrycode,dicuserid,language,title,content,description,commitdatetime,enable) " +
            "values({db_entryid},{db_entryCode},{db_userid},'CN',{db_title},{db_content},{db_description},{db_commitDatetime},1)").on(
            "db_entryid" -> entryId,
            "db_entryCode" -> comment.forEntryCode,
            "db_userid" -> comment.dicUserId,
            "db_title" -> comment.title,
            "db_content" -> comment.content,
            "db_description" -> comment.description,
            "db_commitDatetime" -> commitDatetime).executeInsert()

          println("result = " + result)
          return if (result.get > 0) result.get.toInt else -1
      }
    }catch{
      case ex:Exception => {
        println(ex.getMessage)
        println(ex.getCause)
        return -1
      }
    }
  }

  def deleteComment(oldId:Int):Int = {
    println("deleteComment oldId = " + oldId)
    if(oldId<0){
      throw(new Exception(s"传入的 oldId($oldId)不合法"))
    }

    try {
      val commitDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date)
      db.withConnection {
        implicit c: java.sql.Connection =>
          val result: Option[Long] =  SQL("update comments set enable=0 " +
            "where id={db_oldId}").on(
            "db_oldId" -> oldId,
            "db_commitDatetime" -> commitDatetime
          ).executeInsert()
          return if (result.get > 0) result.get.toInt else -1
      }
    }catch{
      case ex: Exception => {
        println(ex.getMessage + " " + ex.getCause)
        throw ex
      }
    }
  }

}
