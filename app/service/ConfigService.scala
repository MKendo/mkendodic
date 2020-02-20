package service

import anorm.{SQL, SqlParser}
import javax.inject.Inject
import play.api.db.DBApi

class ConfigService @Inject()(dbapi: DBApi) {

  private val db = dbapi.database("default")

  /**
    * 暂时没有用到
    * @return
    */
  def findWxOpenidUrl:String = {
    db.withConnection {
      implicit c:java.sql.Connection =>
        val wxurl: Option[String] = SQL("select value from configs where code='wxgetopenid'").as(
          SqlParser.str("value").singleOpt)
        return wxurl.getOrElse("")
    }
  }

}
