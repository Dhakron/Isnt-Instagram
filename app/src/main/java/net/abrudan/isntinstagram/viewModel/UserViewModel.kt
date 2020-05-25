package net.abrudan.isntinstagram.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.model.value.IntegerValue
import net.abrudan.isntinstagram.model.*
import org.jetbrains.anko.doAsync

class UserViewModel : ViewModel() {
    private val TAG = "User-View-Model"
    private val repository= Repositories()
    private var postList : MutableLiveData<List<Post?>> = MutableLiveData()
    private var auth= FirebaseAuth.getInstance()
    private var userData:MutableLiveData<User> = MutableLiveData()
    var followUser : MutableLiveData<Boolean> = MutableLiveData()
    private var loadingData=false
    var isLiking=false
    private var lastPost: DocumentSnapshot?=null

    fun getUserData(uid:String):MutableLiveData<User>{
        repository.getUserData(uid).addOnCompleteListener{data->
            var user=data.result?.toObject(User::class.java)?:User()
            user.uid=(data.result?.id?:"0")
            Log.e(TAG,user.uid?:"")
            repository.getUrl(user?.imageProfile?:"").addOnSuccessListener{uri->
                user?.profileImgURI=uri
            }.addOnFailureListener {
                Log.e(TAG,"Error Imagen")
            }.continueWith{
                userData.value=user
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo User")
            userData.value= User()
        }
        return userData
    }
    fun getFollow():MutableLiveData<Boolean>{
        return followUser
    }
    fun loadFollow(uid:String){
        repository.getFollower(auth.currentUser!!.uid, uid).addOnSuccessListener { doc ->
            followUser.value = doc.exists()
        }.addOnFailureListener {
            followUser.value=false
        }
    }
    fun getUID():String{
        return auth.currentUser?.uid!!
    }
    fun follow(uid:String){
        repository.follow(uid).addOnCompleteListener{
            if(it.isSuccessful){
                Log.e(TAG,"Ahora le sigues")
            }else{
                Log.e(TAG,"Error, ya le seguias")
            }
        }
    }
    fun unFollow(uid:String){
        repository.unFollow(uid).addOnCompleteListener{
            if(it.isSuccessful){
                Log.e(TAG,"Ahora no le sigues")
            }else{
                Log.e(TAG,"Error, no le seguias")
            }
        }
    }
    fun getAllPosts():MutableLiveData<List<Post?>>{
        return postList
    }
    fun loadAllPosts(uid:String){
        loadingData=true
        repository.getUserPosts(uid).addOnCompleteListener {documents->
            var list= mutableListOf<Post?>()
            documents.result?.documents?.map {
                    var temPost=it.toObject(Post::class.java)
                    temPost?.id=it.id
                    temPost?.originalId=it.id
                    temPost?.mediaUri= MutableList(temPost?.media?.size!!){"".toUri()}
                    list.add(temPost)
                    val currentPost=list.size
                    repository.getUrl(temPost?.thumb!!).continueWith {thumbUri->
                        if(thumbUri.isSuccessful){
                            list[currentPost-1]!!.thumbUri=thumbUri.result
                        }
                    }.continueWith{
                        repository.getLike(temPost.uid!!,temPost.originalId!!,auth.currentUser!!.uid).continueWith {like->
                            if(like.isSuccessful){
                                if(like.result!!.exists()){
                                    postList.value!![currentPost-1]?.liked=true
                                }
                            }
                        }
                    }
                    temPost?.media?.map {path->
                        var position= temPost.media?.lastIndexOf(path)
                        repository.getUrl(path).addOnSuccessListener{uri->
                            list[currentPost-1]!!.mediaUri[position!!]=uri
                        }.addOnFailureListener{
                            Log.e(TAG,temPost.media.toString())
                        }.continueWith{
                            postList.value=list
                        }
                    }
                }
            if(documents.result?.documents?.size!! > 0){
                lastPost=documents?.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= postList.value
                if(tempList.isNullOrEmpty()){
                    var temp2= mutableListOf<Post>()
                    temp2!!.add(Post(
                        uid="",
                        id = "",
                        mediaUri = mutableListOf<Uri>(),
                        thumbUri = "".toUri(),
                        media = mutableListOf()
                    ))
                    postList.value=temp2
                    temp2.clear()
                    postList.value=temp2
                }
                postList.value=tempList
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo POSTS")
        }
    }
    fun loadingData():Boolean{
        return loadingData
    }
    fun loadAllPostsFromLast(uid:String){
        if(loadingData||lastPost==null)return
        loadingData=true
        repository.getUserPostsFrom(uid,lastPost!!).addOnCompleteListener {documents->
            var list= postList.value!!.toMutableList()
            documents.result?.documents?.map {
                var temPost=it?.toObject(Post::class.java)
                temPost?.id=it.id
                temPost?.originalId=it.id
                temPost?.mediaUri= MutableList(temPost?.media?.size!!){"".toUri()}
                list.add(temPost)
                postList.value=list
                val currentPost=postList.value!!.size
                repository.getUrl(temPost?.thumb!!).continueWith{url->
                    if(url.isSuccessful){
                        postList.value!![currentPost-1]!!.thumbUri=url.result
                    }
                    repository.getLike(temPost.uid!!,temPost.originalId!!,auth.currentUser!!.uid).continueWith {like->
                        if(like.isSuccessful){
                            if(like.result!!.exists()){
                                postList.value!![currentPost-1]?.liked=true
                            }
                        }
                    }
                }
                temPost?.media?.map {path->
                    var position= temPost.media?.lastIndexOf(path)
                    repository.getUrl(path).continueWith{task->
                        if(task.isSuccessful){
                            postList.value!![currentPost-1]!!.mediaUri[position!!]=task.result!!
                        }
                    }
                }
            }
            if(documents.result!!.documents.size>0){
                lastPost=documents.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= postList.value
                postList.value=tempList
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo POSTS")
        }
    }

    fun likePost(post:Post){
        if(post.liked!=true){
            repository.likePost(post.uid!!, post.originalId!!)
            var tempList = postList.value?.toMutableList()
            tempList?.remove(post)
            post.liked = true
            post.likes = post.likes?.plus(1)
            tempList?.add(post)
            postList.value=tempList
        }else {
            repository.unLikePost(post.uid!!, post.originalId!!)
            var tempList = postList.value?.toMutableList()
            tempList?.remove(post)
            post.liked = false
            post.likes = post.likes?.minus(1)
            tempList?.add(post)
            postList.value=tempList
        }
        removeAnimatioln(post)
    }
    fun removeAnimatioln(post:Post){
        doAsync {
            Thread.sleep(500)
            var tempList = postList.value?.toMutableList()
            tempList?.remove(post)
            post.likeClick = null
            tempList?.add(post)
            postList.value=tempList
        }
    }
    fun deletePost(data:Post){
        repository.deletePost(data.originalId!!)
    }
}