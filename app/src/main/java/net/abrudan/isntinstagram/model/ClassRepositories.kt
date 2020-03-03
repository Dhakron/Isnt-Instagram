package net.abrudan.isntinstagram.model

import android.app.Application
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

class HomeRepositories(aplication : Application) {
    val user= FirebaseAuth.getInstance()
    val functions = FirebaseFunctions.getInstance(FirebaseApp.getInstance(),"europe-west1")
    val db=FirebaseFirestore.getInstance()
    val TAG="Home-Repository"
    //Get allPosts
    fun getAllPosts(): Task<QuerySnapshot> {
        val ref = "UsersData/" + user.uid.toString() + "/FollowingPosts"
        Log.e(TAG, "He pedido los datos a: {$ref}")
        return db.collection(ref).orderBy("date", Query.Direction.DESCENDING)
            .limit(10).get()
    }
    fun getPost(ref:String,docID:String): Task<DocumentSnapshot> {
        Log.e(TAG, "He pedido los datos a: {$ref}")
        return db.collection(ref).document(docID).get()
    }
}
class UserRepositories{
    val user= FirebaseAuth.getInstance()
    val functions = FirebaseFunctions.getInstance(FirebaseApp.getInstance(),"europe-west1")
    val db=FirebaseFirestore.getInstance()
    val storage=FirebaseStorage.getInstance()

    //function set UserID
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
    fun getAllUserPosts(uid:String):Task<QuerySnapshot>{
        val ref= "UsersData/${uid}/Posts"
        return db.collection(ref).orderBy("date",Query.Direction.DESCENDING)
            .limit(10).get()
    }
    fun addPost(tittle:String,media:String):Task<HttpsCallableResult>{
        val data= hashMapOf(
            "tittle" to tittle,
            "media" to media
        )
        return functions.getHttpsCallable("addPost").call(data)
            .continueWith { task->
                return@continueWith task.result
            }
    }
    fun deletePost(postID:String,media:String):Task<HttpsCallableResult>{
        val data= hashMapOf(
            "postID" to postID
        )
        return functions.getHttpsCallable("deletePost").call(data)
            .continueWith { task->
                return@continueWith task.result
            }
    }
    fun searchUser(userID:String):Task<QuerySnapshot>{
        val ref = "UsersInfo"
        return db.collection(ref).whereArrayContains("search",userID)
            .limit(20).get()
    }
    fun getUserInfo(uid:String):Task<DocumentSnapshot>{
        val ref= "UsersInfo/{$uid}"
        return db.document(ref).get()
    }
    fun getUserData(uid:String):Task<DocumentSnapshot>{
        val ref= "UsersData"
        return db.collection(ref).document(uid).get()
    }
    fun getAllFollowers(uid:String):Task<QuerySnapshot>{
        val ref="UsersData/${uid}/Followers"
        return db.collection(ref).get()
    }
    fun getAllFollows(uid:String):Task<QuerySnapshot>{
        val ref="UsersData/${uid}/Following"
        return db.collection(ref).get()
    }
    fun getUrl(path:String): Task<Uri> {
        return storage.reference?.child(path).downloadUrl
    }
}