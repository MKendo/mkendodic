package service

import java.text.SimpleDateFormat
import java.util.Date

import anorm.{Macro, RowParser, SQL}
import javax.inject.Inject
import model.User
import play.api.db.DBApi

class UserService @Inject()(dbapi: DBApi) {

  private val db = dbapi.database("default")

  def findByWxOpenid(openid: String): User ={
    println(s"findByWxOpenid_openid = $openid")
    if(openid==null || openid.equals("null")){
      println("findByWxOpenid 时传入的openid为空")
      return null
    }

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          val newsParser: RowParser[User] = Macro.namedParser[User]
          return SQL("select * from users where wxopenid={db_openid}")
            .on("db_openid" -> openid)
            .as(newsParser.single)
      }
    }catch{
      case ex:Exception =>{
        println("findByWxOpenid() == null " + ex.getMessage)
        return null;
      }
    }

  }

  def addUser(user: User):String = {

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          SQL("insert into users(name,mobile,password,wxopenid,wxname,wximgurl,commitdatetime,description,enable) " +
            "values({db_name},{db_mobile},{db_password},{db_wxopenid},{db_wxname},{db_wximgurl},{db_commitdatetime},{db_description},1)").on(
            "db_name" -> user.name,
            "db_mobile" -> user.mobile,
            "db_password" -> user.password,
            "db_wxopenid" -> user.wxOpenId,
            "db_wxname" -> user.wxName,
            "db_wximgurl" -> user.wxImgUrl,
            "db_commitdatetime" -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date),
            "db_description" -> user.description
          ).executeInsert()
      }
      val dbuser = findByWxOpenid(user.wxOpenId)
      return "SUCCESS_" + dbuser.id
    }catch
      {
        case ex:Exception => {
          return "写入数据库时出错：" + ex.getMessage
        }
      }
    return "未知问题，请联系管理员或重试一次。";

  }


  def updateWxUser(userId: Int, user: User):String = {
    println("userId = " + userId)
    println("user id = " + user.id)
    println("user wxName = " + user.wxName)
    println("user wxOpenId = " + user.wxOpenId)
    println("user wxImgUrl = " + user.wxImgUrl)

    if(userId<0){
      return s"传入的userid($userId)不合法"
    }

    if(user==null){
      return "传入的user不能为空";
    }

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          SQL("update users set wxname={db_wxname},wxopenid={db_openid},wximgurl={db_wximgurl} " +
            "where id={db_userId}").on(
            "db_wxname" -> user.wxName,
            "db_openid" -> user.wxOpenId,
            "db_wximgurl" -> user.wxImgUrl,
            "db_userId" -> userId
          ).executeInsert()
          return "SUCCESS_" + userId
      }
    }catch{
      case ex: Exception => {
        println(ex.getMessage)
        return "更新会员信息时发生错误了:"+ex.getMessage;
      }
    }
  }

}
