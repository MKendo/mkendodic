package model

case class User(id:Int,
                name: String,
                mobile: String,
                password: String,
                wxOpenId: String,
                wxUnionId: String,
                wxName: String,
                wxImgUrl: String,
                description: String)
