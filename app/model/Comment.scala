package model

case class Comment(id:Int,
                   forEntryId:Int,
                   forEntryCode:String,
                   dicUserId:Int,
                   dicUserWxname:String,
                   dicUserWximgurl: String,
                   language:String,
                   title:String,
                   content:String,
                   description:String,
                   commitDatetime:String)
