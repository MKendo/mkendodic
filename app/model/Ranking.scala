package model

case class Ranking(
                    ranking: Int, //名次
                    userId: Int,
                    name: String,
                    imgUrl: String,
                    total: Int,
                    updateDatetime:String
                  )
