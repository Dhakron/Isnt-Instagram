package net.abrudan.isntinstagram.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.StorageReference
import kotlinx.android.parcel.Parcelize
import net.abrudan.isntinstagram.adapters.AdapterPostImageItem
import net.abrudan.isntinstagram.adapters.CommentsAdapter

@Entity
data class Post(
    @PrimaryKey(autoGenerate = true)
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
@Entity
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
    var bio:String="",
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
)
@Entity
data class UserInfo(
    var name:String?="",
    var userID:String?=null,
    var imageProfile:String?="",
    var profileImgURI:Uri?=null,
    var userUID:String?=null,
    var followYou:Boolean?=null,
    var date:Long?=null,
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
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

data class Activities(
    var uid:String?=null,
    var userRef:String?=null,
    var postRef:String?=null
)

data class Comment(
    var owner:String?=null,
    var thumb:String?="",
    var profileImgURI:Uri?=null,
    var uid:String?=null,
    var date:Long?=null,
    var comment:String?=null,
    var likes:Int?=0,
    var replies:Int?=0,
    var liked:Boolean?=false,
    var viewReplies:Boolean=false,
    var isReply:Boolean=false,
    var repliesList: MutableList<Comment?> = mutableListOf(),
    var postRef:String?=null,
    var replyTo:String?=null,
    var commentRef:String?=null,
    var lastReply: DocumentSnapshot?=null,
    var loadingReply:Boolean=false,
    var replyRef:String?=null
)