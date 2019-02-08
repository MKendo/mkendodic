package service

import play.api.Logger
import anorm._
import anorm.{SQL, SqlParser}
import javax.inject.Inject
import model.ValidateDescription
import play.api.db.DBApi

/**
  * 指定memberid的成员服务
  * @param userid
  * @param dbapi
  */
class MemberService @Inject()(dbapi: DBApi, userid: String) {

  private val logger = Logger(getClass)
  private val db = dbapi.database("default")
  private val memberid = userid

  /**
    * 查找成员名字
    * @return
    */
  def findName(): String ={
    db.withConnection { implicit c:java.sql.Connection =>
      val name: Option[String] = SQL("select name from members where userid={memberid}").on(
        "memberid" -> memberid).as(
        SqlParser.str("members.name").singleOpt)
      return if(name.isEmpty) "" else name.get
    }
  }

  /**
    * 查询成员有效期
    * @return
    */
  def findEndValidate(): String ={
     db.withConnection { implicit c:java.sql.Connection =>
      val endvalidate : Option[String] = SQL("select mv.endvalidate from member_validates mv join members m on mv.memberid=m.id and m.userid={memberid}").on(
        "memberid" -> memberid).as(
        SqlParser.str("member_validates.endvalidate").singleOpt)
       return if(endvalidate.isEmpty) "" else endvalidate.get
    }
  }

  def findDescriptions(): Array[String] = {
    db.withConnection { implicit c:java.sql.Connection =>
      val description : Option[String] = SQL("select mv.description from member_validates mv join members m on mv.memberid=m.id and m.userid={memberid}").on(
        "memberid" -> memberid).as(
        SqlParser.str("member_validates.description").singleOpt)
      return if(description.isEmpty) "".split("##") else description.get.split("##")
    }
  }

}
