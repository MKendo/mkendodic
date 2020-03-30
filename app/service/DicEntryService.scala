package service

import anorm.{Macro, RowParser, SQL, SqlParser}
import javax.inject.Inject
import model.DicEntry
import play.api.db.DBApi

class DicEntryService @Inject()(dbapi: DBApi) {

  private val db = dbapi.database("default")

  def searchEntries(key:String): List[DicEntry] ={
    db.withConnection {
      implicit c: java.sql.Connection =>
        val newsParser: RowParser[DicEntry] = Macro.namedParser[DicEntry]

        val result: List[DicEntry] = SQL(
          "select * from dicentries " +
            "where code in(select code from dicentries where enable=1 and (title like {db_key} or code like {db_key}))" +
            "order by id asc").on(
          "db_key" -> s"%$key%").as(newsParser.*)
        return result
    }
    return Nil;
  }

  def searchByCode(code:String): List[DicEntry] ={
    db.withConnection {
      implicit c: java.sql.Connection =>
        val newsParser: RowParser[DicEntry] = Macro.namedParser[DicEntry]

        val result: List[DicEntry] = SQL(
          "select * from dicentries where code in(select code from dicentries where code = {db_code}) and enable=1 order by id asc").on(
          "db_code" -> code).as(newsParser.*)
        return result
    }
    return Nil;
  }

  def searchCodeByRandom(): Option[String] = {
    println("searchCodeByRandom.......")
    db.withConnection {
      implicit c: java.sql.Connection =>
        val result: Option[String] = SQL(
          "SELECT code FROM dicentries WHERE enable=1 ORDER BY RANDOM() limit 1").as(
          SqlParser.str("dicentries.code").singleOpt)
        println(s"result = $result")
        return result
    }
  }

}
