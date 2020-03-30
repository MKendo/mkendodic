package model

case class Ranking(
                    ranking: Int, //名次
                    uuserId: Int, //注意，此处不可改名为userid，因为点赞功能查询出两个userid（用户表里一个，素振表里一个，点赞表里还有一个，sqlite对sql支持不够强大识别不同所以。。详见：SuburiService.findRankingList()
                    name: String,
                    imgUrl: String,
                    total: Int,
                    updateDatetime:String,
                    zan: Int
                  )
