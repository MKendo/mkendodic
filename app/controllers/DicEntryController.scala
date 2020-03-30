package controllers

import javax.inject.Inject
import model.{Comment, DicEntry}
import play.api.db.DBApi
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import service.{CommonUnit, DicCommentService, DicEntryService}

class DicEntryController @Inject()(cc: ControllerComponents)(dbapi: DBApi) extends AbstractController(cc) {

  /**
    * 搜索
    * @param key
    * @return
    */
  def searchEntries(key:String) = Action {
    implicit request => {
      println("searchEntries........")
      println(s"key = $key")
      if(key.isEmpty){
        throw(new Exception("searchEntries时传入的key不能为空"))
      }
      val entryService = new DicEntryService(dbapi)

      implicit val entryWrites = new Writes[DicEntry] {
        def writes(entry: DicEntry) = Json.obj(
          "id" -> entry.id,
          "code" -> entry.code,
          "title" -> entry.title,
          "content" -> entry.content,
          "language" -> entry.language,
          "description" -> entry.description)
      }

      val entries = entryService.searchEntries(if(key.startsWith(":")) key.substring(1) else key);
      println("entries count = " + entries.length)
      val entriesJson = Json.toJson(entries)
      Ok(entriesJson)
    }
  }

  def searchEntryCodeRandom() = Action {
    implicit request => {
      println("searchEntryCodeRandom........")
      val entryService = new DicEntryService(dbapi)
      val code = entryService.searchCodeByRandom().getOrElse("")
      println(s"code = $code")
      Ok(code)
    }
  }

  def searchByCode(code:String) = Action {
    implicit request => {
      println("searchByCode........")
      println(s"code = $code")
      if(code.isEmpty){
        throw(new Exception("searchByCode时传入的code不能为空"))
      }
      val entryService = new DicEntryService(dbapi)

      implicit val entryWrites = new Writes[DicEntry] {
        def writes(entry: DicEntry) = Json.obj(
          "id" -> entry.id,
          "code" -> entry.code,
          "title" -> entry.title,
          "content" -> entry.content,
          "language" -> entry.language,
          "description" -> entry.description)
      }

      val entries = entryService.searchByCode(if(code.startsWith(":")) code.substring(1) else code);
      println("entries count = " + entries.length)
      val entriesJson = Json.toJson(entries)
      Ok(entriesJson)
    }
  }

  def searchComments(entryCode:String) = Action {
    implicit request => {
      println("searchComments........")
      println(s"entryCode = $entryCode")
      if(entryCode.isEmpty){
        throw(new Exception("searchComments时传入的entryCode不能为空"))
      }
      val commentService = new DicCommentService(dbapi)

      implicit val commentWrites = new Writes[Comment] {
        def writes(comment: Comment) = Json.obj(
          "id" -> comment.id,
          "forEntryId" -> comment.forEntryId,
          "forEntryCode" -> comment.forEntryCode,
          "dicUserId" -> comment.dicUserId,
          "dicUserWxname" -> comment.dicUserWxname,
          "dicUserWximgurl" -> comment.dicUserWximgurl,
          "language" -> comment.language,
          "title" -> comment.title,
          "content" -> comment.content,
          "description" -> comment.description,
          "commitDatetime" -> comment.commitDatetime)
      }

      val comments = commentService.searchCommentsByEntryCode(if(entryCode.startsWith(":")) entryCode.substring(1) else entryCode);
      println("entries count = " + comments.length)
      val entriesJson = Json.toJson(comments)
      Ok(entriesJson)
    }
  }

  def searchNewComments() =  Action {
    implicit request => {
      println("searchNewComments........")
      val commentService = new DicCommentService(dbapi)

      implicit val commentWrites = new Writes[Comment] {
        def writes(comment: Comment) = Json.obj(
          "id" -> comment.id,
          "forEntryId" -> comment.forEntryId,
          "forEntryCode" -> comment.forEntryCode,
          "dicUserId" -> comment.dicUserId,
          "dicUserWxname" -> comment.dicUserWxname,
          "dicUserWximgurl" -> comment.dicUserWximgurl,
          "language" -> comment.language,
          "title" -> comment.title,
          "content" -> comment.content,
          "description" -> comment.description,
          "commitDatetime" -> comment.commitDatetime)
      }

      val comments = commentService.searchNewComments(20)
      println("entries count = " + comments.length)
      val entriesJson = Json.toJson(comments)
      Ok(entriesJson)
    }
  }

  def fanyiPost() = Action {
    implicit request => {
      println("fanyiPost.....") //TODO 改为有ID就是修改

      try {
        val dicUserIdStr = request.body.asFormUrlEncoded.get("fyuserid").head.toString
        println(s"dicUserIdStr = $dicUserIdStr")
        val dicUserid = if(CommonUnit.isIntByRegex(dicUserIdStr)) dicUserIdStr.toInt else -1
        val entryCode = request.body.asFormUrlEncoded.get("fyentrycode").head.toString
        println(s"entryCode = $entryCode")
        val title = request.body.asFormUrlEncoded.get("fytitle").head.toString
        println(s"title = $title")
        val content = request.body.asFormUrlEncoded.get("fycontent").head.toString
        println(s"content = $content")
        var commentId = ""
        try {
          commentId = request.body.asFormUrlEncoded.get("commentid").head.toString
          println(s"commentId = $commentId")
        }catch {
          case ex: Exception => {
            //nothing 第一次新增的时候，commentId是允许没有的
          }
        }

        if(!CommonUnit.isIntByRegex(dicUserIdStr) || dicUserid.toInt<=0){
          Ok("传入的 userid 不合法：" + dicUserIdStr)
        }

        if(entryCode.isEmpty){
          Ok("传入的 entryCode 不合法：" + entryCode)
        }

        if(title.isEmpty){
          Ok("传入的 title 不能为")
        }

        if(content.isEmpty){
          Ok("传入的 content 不能为空")
        }

        //增加翻译到comments
        val commentService = new DicCommentService(dbapi)
        val comment = Comment(-1,
          -1,
          entryCode,
          dicUserid,
          "",
          "",
          "CN",
          title:String,
          content:String,
          "",
          "")

        var result = -1

        //如果数据库里已存在，则先删除掉老的enable=0
        println("commentId = " + commentId)
        if(!commentId.isEmpty && CommonUnit.isIntByRegex(commentId) && commentId.toInt>0){
          println("先删除")
          val dbComment = commentService.findById(commentId.toInt)
          if(dbComment!=null){
            commentService.deleteComment(commentId.toInt)
          }
        }

        //新增
        result = commentService.createComment(comment)

        if (result > 0) {
          Ok("SUCCESS")
        }else{
          Ok("保存翻译时出错，返回值为:" + result.toString)
        }
      }catch{
        case ex2: NoSuchElementException => {
          println(ex2.getMessage)
          println(ex2.getCause)
          Ok("更新会员信息时发生错误了:"+ex2.getMessage + " " + ex2.getCause)
        }
        case ex: Exception => {
          println(ex.getMessage)
          println(ex.getCause)
          Ok("更新会员信息时发生错误了:"+ex.getMessage + " " + ex.getCause)
        }
      }
    }
  }

  def deleteComment(id:String) = Action {
    implicit request => {
      println("deleteComment......")
      val commentService = new DicCommentService(dbapi)
      var cid = ""
      if(id.startsWith(":")){
        cid = id.substring(1)
      }
      if(!cid.isEmpty && CommonUnit.isIntByRegex(cid) && cid.toInt>0) {
        val result = commentService.deleteComment(cid.toInt)
        println(s"result = $result")
        if(result>0){
          Ok("SUCCESS")
        }else{
          Ok(s"没有找到要删除的comments: id = $cid")
        }
      }else{
        Ok(s"删除comments时发生错误：id = $cid")
      }
    }
  }

}
