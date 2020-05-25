package net.abrudan.isntinstagram.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.abrudan.isntinstagram.model.Post
import net.abrudan.isntinstagram.model.Repositories
import org.jetbrains.anko.doAsync


class HomeViewModel : ViewModel() {
    private val TAG = "Home-View-Model"
    private val repository= Repositories()
    private var postList : MutableLiveData<MutableList<Post?>> = MutableLiveData()
    private var loadingData=false
    private var auth= FirebaseAuth.getInstance()
    var isLiking=false
    private var lastPost:DocumentSnapshot?=null
    fun getAllPosts():MutableLiveData<MutableList<Post?>>{
        return postList
    }
    fun loadAllPosts(){
        loadingData=true
        repository.getPosts().addOnCompleteListener {documents->
            var list= mutableListOf<Post?>()
            documents.result?.documents?.map {
                repository.getPost(it.get("userRef") as String,it.get("postID") as String).addOnCompleteListener{post->
                    var temPost=post.result?.toObject(Post::class.java)
                    temPost?.liked=it.getBoolean("liked")
                    temPost?.id=it.id
                    temPost?.originalId=it.getString("postID")
                    temPost?.mediaUri= MutableList(temPost?.media?.size!!){"".toUri()}
                    list.add(temPost)
                    val currentPost=list.size
                    repository.getUrl(temPost?.thumb!!).continueWith {thumbUri->
                        if(thumbUri.isSuccessful){
                            list[currentPost-1]!!.thumbUri=thumbUri.result
                        }
                    }.continueWith{
                        repository.getLike(temPost.uid!!,temPost.originalId!!,auth.currentUser!!.uid).addOnSuccessListener {like->
                            if(like.exists()){
                                postList.value!![currentPost-1]?.liked=true
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
            }
            if(documents.result!!.documents.size>0){
                lastPost=documents.result?.documents?.last()
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
                    postList.value=temp2.toMutableList()
                    temp2.clear()
                    postList.value=temp2.toMutableList()
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
    fun loadAllPostsFromLast(){
        if(loadingData||lastPost==null)return
        loadingData=true
        repository.getPostsFrom(lastPost!!).addOnCompleteListener {documents->
            var list= postList.value!!.toMutableList()
            documents.result?.documents?.map {
                repository.getPost(it.get("userRef") as String,it.get("postID") as String).addOnCompleteListener{post->
                    var temPost=post.result?.toObject(Post::class.java)
                    temPost?.liked=it.getBoolean("liked")
                    temPost?.id=it.id
                    temPost?.originalId=it.getString("postID")
                    temPost?.mediaUri= MutableList(temPost?.media?.size!!){"".toUri()}
                    list.add(temPost)
                    postList.value=list
                    var currentPost=postList.value!!.size
                    repository.getUrl(temPost?.thumb!!).continueWith{thumUri->
                        if(thumUri.isSuccessful){
                            postList.value!![currentPost-1]!!.thumbUri=thumUri.result
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

}