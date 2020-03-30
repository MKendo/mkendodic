package service

import java.text.SimpleDateFormat
import java.util.Date

import anorm.{Macro, RowParser, SQL}
import javax.inject.Inject
import model.{User, WxUseridInfo}
import play.api.db.DBApi

class DicUserService @Inject()(dbapi: DBApi) {

  private val db = dbapi.database("default")

  def findByUserid(userid: String): User ={
    println(s"findByUserid_userid = $userid")
    if(!CommonUnit.isIntByRegex(userid)) {
      println(s"findByUserid时传入的userid不合法：$userid")
      return null
    }else{
      try {
        db.withConnection {
          implicit c: java.sql.Connection =>
            val newsParser: RowParser[User] = Macro.namedParser[User]
            return SQL("select * from dicusers where id={db_userid}")
              .on("db_userid" -> userid)
              .as(newsParser.single)
        }
      }catch{
        case ex:Exception =>{
          println("findByUserid() == null " + ex.getMessage)
          return null;
        }
      }
    }
  }

  def findByWxDicOpenid(openid: String): User ={
    println(s"findByWxDicOpenid = $openid")
    if(openid==null || openid.equals("null")){
      println("findByWxDicOpenid 时传入的openid为空")
      return null
    }

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          val newsParser: RowParser[User] = Macro.namedParser[User]
          val users = SQL("select * from dicusers where wxopenid={db_openid} ")
            .on("db_dicopenid" -> openid)
            .as(newsParser.*)
          println("user = " + users)
          if(users.length==1) {
            return users(0)
          }else{
            println(s"根据wxopenid($openid)查询到多个user:"+users.length)
            return null;
          }
      }
    }catch{
      case ex:Exception =>{
        println("findByWxDicOpenid() == null " + ex.getMessage + " " + ex.getCause)
        throw new Exception(ex.getMessage,ex.getCause);
      }
    }
  }

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
          val users = SQL("select * from dicusers where wxopenid={db_openid} ")
            .on("db_openid" -> openid)
            .as(newsParser.*)
          println("user = " + users)
          if(users.length==1) {
            return users(0)
          }else{
            println(s"根据wxopenid($openid)查询到多个user:"+users.length)
            return null;
          }
      }
    }catch{
      case ex:Exception =>{
        println("findByWxOpenid() == null " + ex.getMessage + " " + ex.getCause)
        throw new Exception(ex.getMessage,ex.getCause);
      }
    }
  }

  def addUser(user: User):String = {

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          SQL("insert into dicusers(name,mobile,password,wxopenid,wxunionid,wxname,wximgurl,commitdatetime,description,enable) " +
            "values({db_name},{db_mobile},{db_password},{db_wxopenid},{db_wxunionid},{db_wxname},{db_wximgurl},{db_commitdatetime},{db_description},1)").on(
            "db_name" -> user.name,
            "db_mobile" -> user.mobile,
            "db_password" -> user.password,
            "db_wxopenid" -> user.wxOpenId,
            "db_wxunionid" -> user.wxUnionId,
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

  def registerWxUserIdInfo(wxUseridInfo: WxUseridInfo): String ={
    try {
      if(wxUseridInfo==null || wxUseridInfo.openId.isEmpty){
        return s"注意用户信息时，传入的wxUseridInfo不合法：" + wxUseridInfo
      }else {
        db.withConnection {
          implicit c: java.sql.Connection =>
            val result: Option[Long] =SQL("insert into dicusers(name,mobile,password,wxopenid,wxunionid,wxname,wximgurl,commitdatetime,description,enable) " +
              "values('','','',{db_wxopenid},{db_wxunionid},'','',{db_commitdatetime},'',1)").on(
              "db_wxopenid" -> wxUseridInfo.openId,
              "db_wxunionid" -> wxUseridInfo.unionId,
              "db_commitdatetime" -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date)
            ).executeInsert()
            println("SUCCESS_" + String.valueOf(result.get))
            return "SUCCESS_" + String.valueOf(result.get)
        }
      }
    }catch
      {
        case ex:Exception => {
          println("写入数据库时出错：" + ex.getMessage)
          throw new Exception("写入数据库时出错：" + ex.getMessage,ex.getCause)
        }
      }
    return "未知问题，请联系管理员或重试一次。"
  }

  def updateWxUnionId(userId:Int,wxunionId:String): String ={
    if(userId<0){
      return s"传入的userid($userId)不合法"
    }

    if(wxunionId.isEmpty || wxunionId.isEmpty){
      println(s"更新用户信息时传入的wxunionId($wxunionId)不能为空)")
      return s"更新用户信息时传入的wxunionId($wxunionId)不能为空"
    }

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          SQL("update dicusers set wxunionid={db_wxunionid} " +
            "where id={db_userId}").on(
            "db_wxunionid" -> wxunionId,
            "db_userId" -> userId
          ).executeInsert()
          return "SUCCESS"
      }
    }catch{
      case ex: Exception => {
        println(ex.getMessage)
        return s"用户($userId)的wxunionid信息更新失败:"+ex.getMessage;
      }
    }
  }

  def updateSimpleWxUserInfo(userId: Int, wxname: String, wximgurl: String): String ={
    if(userId<0){
      return s"传入的userid($userId)不合法"
    }

    if(wxname.isEmpty || wximgurl.isEmpty){
      println(s"更新用户信息时传入的wxname($wxname),wximgurl($wximgurl) 有一项为空，实际未更新")
      return "SUCCESS_" + userId
    }

    try {
      db.withConnection {
        implicit c: java.sql.Connection =>
          SQL("update dicusers set wxname={db_wxname},wximgurl={db_wximgurl} " +
            "where id={db_userId}").on(
            "db_wxname" -> wxname,
            "db_wximgurl" -> wximgurl,
            "db_userId" -> userId
          ).executeInsert()
          return "SUCCESS_" + userId
      }
    }catch{
      case ex: Exception => {
        println(ex.getMessage+" "+ex.getCause)
        return "更新会员信息时发生错误了:"+ex.getMessage+" "+ex.getCause;
      }
    }
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
          SQL("update dicusers set wxname={db_wxname},wxopenid={db_openid},wximgurl={db_wximgurl} " +
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
