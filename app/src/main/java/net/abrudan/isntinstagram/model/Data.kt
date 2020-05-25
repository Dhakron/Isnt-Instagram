package net.abrudan.isntinstagram.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.abrudan.isntinstagram.adapters.AdapterPostImageItem

data class Post(
    var id:String="",
    var tittle:String?="",
    var media:List<String>?=null,
    var mediaUri:MutableList<Uri> = mutableListOf(),
    var thumb:String?="",
    var thumbUri: Uri?=null,
    var liked:Boolean?=null,
    var likes:Int?=0,
    var comments:Int?=0,
    var owner:String?="",
    var uid:String?="",
    var originalId:String?="",
    var likeClick:Boolean?=true,
    var date:Long?=null,
    var adapter:AdapterPostImageItem?=null
)
data class User(
    var name:String?="",
    var userID:String?=null,
    var imageProfile:String?="",
    var numFollowers:Int?=0,
    var numFollows:Int?=0,
    var numPosts:Int?=0,
    var uid:String?=null,
    var profileImgURI:Uri?=null,
    var public:Boolean?=true,
    var followBack:Boolean?=null,
    var bio:String=""
)
data class UserInfo(
    var name:String?="",
    var userID:String?=null,
    var imageProfile:String?="",
    var profileImgURI:Uri?=null,
    var userUID:String?=null,
    var followYou:Boolean?=null,
    var date:Long?=null
    )
@Parcelize
data class MediaPost(
    var imgURI:Uri?=null,
    var name:String?=null,
    var id:Long?=null,
    var folderName:String?=null,
    var date:Long?=null,
    var type:String,
    var selecting:Boolean=false,
    var selected:Boolean=false,
    var selectedPosition:Int=-1,
    var bitMap:Bitmap?=null,
    var front:Boolean=false
    ): Parcelable

data class activities(
    var uid:String?=null,
    var userRef:String?=null,
    var postRef:String?=null
)