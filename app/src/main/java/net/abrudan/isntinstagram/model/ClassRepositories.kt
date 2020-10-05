package net.abrudan.isntinstagram.model

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.lang.Exception

class Repositories {
    val user= FirebaseAuth.getInstance()
    val functions = FirebaseFunctions.getInstance(FirebaseApp.getInstance(),"europe-west1")
    val db=FirebaseFirestore.getInstance()
    val storage=FirebaseStorage.getInstance()


    fun getPosts(): Task<QuerySnapshot> {
        val ref = "UsersData/" + user.uid.toString() + "/FollowingPosts"
        return db.collection(ref).orderBy("date", Query.Direction.DESCENDING)
            .limit(10).get()
    }

    fun getAllUserInfo():Task<QuerySnapshot>{
        val ref="UsersInfo"
        return db.collection(ref).orderBy("date",Query.Direction.DESCENDING).limit(30).get()
    }

    fun getAllUserInfoFrom(doc:DocumentSnapshot):Task<QuerySnapshot>{
        val ref="UsersInfo"
        return db.collection(ref).orderBy("date",Query.Direction.DESCENDING).startAfter(doc).limit(30).get()
    }
    fun getPostsFrom(document:DocumentSnapshot): Task<QuerySnapshot> {
        val ref = "UsersData/" + user.uid.toString() + "/FollowingPosts"
        return db.collection(ref).orderBy("date", Query.Direction.DESCENDING)
            .startAfter(document)
            .limit(10).get()
    }
    fun getPost(ref:String,docID:String): Task<DocumentSnapshot> {
        return db.collection(ref).document(docID).get()
    }
    fun likePost(ownerPost:String,postId:String): Task<HttpsCallableResult> {
        val postRef="UsersData/"+ownerPost+"/Posts/"+postId
        val data = hashMapOf(
            "postRef" to postRef,
            "followingPostId" to ownerPost+postId
        )
        return functions.getHttpsCallable("addLike")
            .call(data)
            .continueWith { task ->
                return@continueWith task.result
            }
    }
    fun unLikePost(ownerPost:String,postId:String): Task<HttpsCallableResult> {
        val postRef="UsersData/"+ownerPost+"/Posts/"+postId
        val data = hashMapOf(
            "postRef" to postRef,
            "followingPostId" to ownerPost+postId
        )
        return functions.getHttpsCallable("deleteLike")
            .call(data)
            .continueWith { task ->
                return@continueWith task.result
            }
    }

    fun setUserID(userID: String): Task<HttpsCallableResult> {
        val data = hashMapOf(
            "userID" to userID
        )
        return functions.getHttpsCallable("setUserID")
            .call(data)
            .continueWith { task ->
                return@continueWith task.result
            }
    }
    fun editUserData(userName: String,bio:String): Task<HttpsCallableResult> {
        val data = hashMapOf(
            "userName" to userName,
            "bio" to bio
        )
        return functions.getHttpsCallable("setUserData")
            .call(data)
            .continueWith { task ->
                return@continueWith task.result
            }
    }
    fun follow(uid:String):Task<HttpsCallableResult> {
        val data= hashMapOf(
            "followUID" to uid
        )
        return functions.getHttpsCallable("follow").call(data)
            .continueWith { task->
                return@continueWith task.result
            }
    }
    fun unFollow(uid:String):Task<HttpsCallableResult> {
        val data= hashMapOf(
            "followUID" to uid
        )
        return functions.getHttpsCallable("unFollow").call(data)
            .continueWith { task->
                return@continueWith task.result
            }
    }
    fun getFollower(uid:String,following:String):Task<DocumentSnapshot>{
        val ref= "UsersData/${uid}/Following/${following}"
        return db.document(ref).get()
    }
    fun getUserPosts(uid:String):Task<QuerySnapshot>{
        val ref= "UsersData/${uid}/Posts"
        return db.collection(ref).orderBy("date",Query.Direction.DESCENDING)
            .limit(10).get()
    }
    fun getUserPostsFrom(uid:String,document: DocumentSnapshot):Task<QuerySnapshot>{
        val ref= "UsersData/${uid}/Posts"
        return db.collection(ref).orderBy("date",Query.Direction.DESCENDING)
            .startAfter(document)
            .limit(10).get()
    }
    fun addPost(tittle:String,media:List<String>):Task<HttpsCallableResult>{
        val data = hashMapOf(
            "tittle" to tittle,
            "media" to media
        )
        return functions.getHttpsCallable("addPost")
            .call(data)
            .continueWith { task ->
                return@continueWith task.result
            }
    }
    fun deletePost(postID:String):Task<HttpsCallableResult>{
        val data= hashMapOf(
            "postID" to postID
        )
        return functions.getHttpsCallable("deletePost").call(data)
            .continueWith { task->
                return@continueWith task.result
            }
    }
    fun getLike(ownerPost: String,postId:String,myUID:String):Task<DocumentSnapshot>{
        val ref= "UsersData/${ownerPost}/Posts/${postId}/Likes/"
        return db.collection(ref).document(myUID).get()
    }
    fun searchUser(userID:String):Task<QuerySnapshot>{
        val ref = "UsersInfo"
        return db.collection(ref).whereArrayContains("dataSearch",userID)
            .limit(30).get()
    }
    fun searchUserFrom(userID:String,doc:DocumentSnapshot):Task<QuerySnapshot>{
        val ref = "UsersInfo"
        return db.collection(ref).whereArrayContains("dataSearch",userID)
            .startAfter(doc).limit(30).get()
    }
    fun getUserInfo(uid:String):Task<DocumentSnapshot>{
        val ref= "UsersInfo/${uid}"
        return db.document(ref).get()
    }
    fun getUserData(uid:String):Task<DocumentSnapshot>{
        val ref= "UsersData"
        return db.collection(ref).document(uid).get()
    }
    fun getAllFollowers(uid:String):Task<QuerySnapshot>{
        val ref="UsersData/${uid}/Followers"
        return db.collection(ref).limit(20).get()
    }
    fun getAllFollowersFrom(uid: String,document: DocumentSnapshot):Task<QuerySnapshot>{
        val ref="UsersData/${uid}/Followers"
        return db.collection(ref)
            .startAfter(document)
            .limit(20).get()
    }
    fun getAllFollows(uid:String):Task<QuerySnapshot>{
        val ref="UsersData/${uid}/Following"
        return db.collection(ref).limit(20).get()
    }
    fun getAllFollowsFrom(uid: String,document: DocumentSnapshot):Task<QuerySnapshot>{
        val ref="UsersData/${uid}/Following"
        return db.collection(ref)
            .startAfter(document)
            .limit(20).get()
    }
    fun getUrl(path:String): Task<Uri> {
        return storage.reference.child(path).downloadUrl
    }
    fun uploadImageProfile(image:Bitmap):UploadTask{
        storage.reference.child(user.currentUser!!.uid+"/profileImage.jpg")
        val refPath=user.currentUser?.uid+"/profileImage.jpg"
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return storage?.reference?.child(refPath)?.putBytes(data,metadata)
    }
    fun addComment(comment:String,postRef:String, replyTo:String?):Task<HttpsCallableResult>{
        val data = hashMapOf(
            "comment" to comment,
            "postRef" to postRef,
            "replyTo" to replyTo
        )
        return functions.getHttpsCallable("addComment")
            .call(data)
            .continueWith { task ->
                return@continueWith task.result
            }
    }
    fun deleteComment(commentRef:String):Task<HttpsCallableResult>{
        val data= hashMapOf(
            "commentRef" to commentRef
        )
        return functions.getHttpsCallable("deleteComment").call(data)
            .continueWith { task->
                return@continueWith task.result
            }
    }
    fun likeComment(commentRef:String): Task<HttpsCallableResult> {
        val data = hashMapOf(
            "commentRef" to commentRef
        )
        return functions.getHttpsCallable("addLikeToComment")
            .call(data)
            .continueWith { task ->
                return@continueWith task.result
            }
    }
    fun unlikeComment(commentRef:String): Task<HttpsCallableResult> {
        val data = hashMapOf(
            "commentRef" to commentRef
        )
        return functions.getHttpsCallable("deleteLikeToComment")
            .call(data)
            .continueWith { task ->
                return@continueWith task.result
            }
    }
    fun getComments(postRef:String): Task<QuerySnapshot> {
        val ref = postRef + "/Comments"
        return db.collection(ref).orderBy("date", Query.Direction.DESCENDING)
            .limit(30).get()
    }
    fun getCommentsFrom(postRef: String,document:DocumentSnapshot): Task<QuerySnapshot> {
        val ref = postRef + "/Comments"
        return db.collection(ref).orderBy("date", Query.Direction.DESCENDING)
            .startAfter(document)
            .limit(30).get()
    }
    fun getReplies(commentRef:String): Task<QuerySnapshot> {
        val ref = "$commentRef/Replies"
        return db.collection(ref).orderBy("date", Query.Direction.DESCENDING)
            .limit(3).get()
    }
    fun getRepliesFrom(commentRef: String,document:DocumentSnapshot): Task<QuerySnapshot> {
        val ref = "$commentRef/Replies"
        return db.collection(ref).orderBy("date", Query.Direction.DESCENDING)
            .startAfter(document)
            .limit(3).get()
    }
    fun getLikeComment(commentRef: String,myUID:String):Task<DocumentSnapshot>{
        val ref= "$commentRef/Likes/"
        return db.collection(ref).document(myUID).get()
    }
    fun getLikeCommentReply(commentRef: String,replyId: String,myUID:String):Task<DocumentSnapshot>{
        val ref= "$commentRef/Replies/$replyId/Likes/"
        return db.collection(ref).document(myUID).get()
    }
}
