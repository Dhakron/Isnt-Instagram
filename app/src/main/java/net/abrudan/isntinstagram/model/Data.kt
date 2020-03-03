package net.abrudan.isntinstagram.model

import android.net.Uri
import java.util.*

data class Post(
    var tittle:String?="",
    var media:String?="",
    var thumb:String?="",
    var likes:Int?=0,
    var comments:Int?=0,
    var owner:String?="",
    var uid:String?="",
    var date:Date?=null
)
data class User(
    var name:String?="",
    var userID:String?=null,
    var imageProfile:String?="",
    var numFollowers:Int?=0,
    var numFollows:Int?=0,
    var numPosts:Int?=0,
    var uid:String?=null,
    var public:Boolean?=true
)
data class UserInfo(
    var name:String?="",
    var userID:String?=null,
    var profileImg:String?="",
    var profileImgURI:Uri?=null,
    var userRef:String?=null
    )

data class activities(
    var uid:String?=null,
    var userRef:String?=null,
    var postRef:String?=null

)