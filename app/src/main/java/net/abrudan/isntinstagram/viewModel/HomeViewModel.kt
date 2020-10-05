package net.abrudan.isntinstagram.viewModel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.*
import coil.Coil
import coil.request.LoadRequest
import coil.request.RequestDisposable
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import net.abrudan.isntinstagram.MainActivity
import net.abrudan.isntinstagram.model.Post
import net.abrudan.isntinstagram.model.Repositories
import org.jetbrains.anko.doAsync
import java.lang.Exception
import java.util.*


class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "Home-View-Model"
    private val repository= Repositories()
    private var postList : MutableLiveData<MutableList<Post?>> = MutableLiveData()
    private var loadingData=false
    private var storage= FirebaseStorage.getInstance()
    private var auth= FirebaseAuth.getInstance()
    private var lastPost:DocumentSnapshot?=null
    private var context=application
    private var imageLoader= Coil.imageLoader(application)
    fun getAllPosts():MutableLiveData<MutableList<Post?>>{
        return postList
    }

    fun loadAllPostsSync() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                var list = mutableListOf<Post?>()
                loadingData = true
                var documents =  withContext(Dispatchers.IO){repository.getPosts().await()}
                var listJobs= mutableListOf<Deferred<Boolean>>()
                documents.documents.map {
                    listJobs.add(async {
                        var post =
                            withContext(Dispatchers.IO){repository.getPost(it.get("userRef") as String, it.get("postID") as String).await()}
                        var temPost = post.toObject(Post::class.java)
                        temPost?.liked = it.getBoolean("liked")
                        temPost?.id = it.id
                        temPost?.originalId = it.getString("postID")
                        temPost?.mediaUri = MutableList(temPost?.media?.size!!) { "".toUri() }
                        temPost?.thumbUri = withContext(Dispatchers.IO){repository.getUrl(temPost?.thumb!!).await()}
                        val request = LoadRequest.Builder(context)
                            .data(temPost?.thumbUri)
                            .build()
                        imageLoader.execute(request).await()
                        var like =
                            withContext(Dispatchers.IO){repository.getLike(temPost.uid!!, temPost.originalId!!, auth.currentUser!!.uid)
                                .await()}
                        if (like.exists()) temPost.liked = true
                        var listMediaJobs = mutableListOf<Deferred<Unit>>()
                        temPost?.media?.map { path ->
                            listMediaJobs.add(async(Dispatchers.IO){
                                var position = temPost.media?.lastIndexOf(path)
                                try {
                                    temPost.mediaUri[position!!] = withContext(Dispatchers.IO){repository.getUrl(path).await()}
                                }catch (e:Exception){
                                    temPost.mediaUri[position!!] = "".toUri()
                                }
                                val request = LoadRequest.Builder(context)
                                    .data(temPost.mediaUri[position!!])
                                    .build()
                                imageLoader.execute(request).await()

                            })
                        }
                        listMediaJobs.awaitAll()
                        list.add(temPost)
                    })
                }
                listJobs.awaitAll()
                if (documents.documents.size > 0) {
                    withContext(Dispatchers.Main) {postList.value=list}
                    lastPost = documents.documents?.last()
                    loadingData = false
                } else {
                    withContext(Dispatchers.Main) {postList.value = emptyList<Post?>().toMutableList()}
                }
            }
        }
    }

    fun loadAllPosts(){
        loadingData=true
        repository.getPosts()
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
                postList.value= emptyList<Post>().toMutableList()
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
            var list= postList.value
            documents.result?.documents?.map {
                repository.getPost(it.get("userRef") as String,it.get("postID") as String).addOnCompleteListener{post->
                    var temPost=post.result?.toObject(Post::class.java)
                    temPost?.liked=it.getBoolean("liked")
                    temPost?.id=it.id
                    temPost?.originalId=it.getString("postID")
                    temPost?.mediaUri= MutableList(temPost?.media?.size!!){"".toUri()}
                    list!!.add(temPost)
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

    fun loadAllPostsFromLastSync(){
        viewModelScope.launch {
            withContext(Dispatchers.Default){
                if(loadingData||lastPost==null)return@withContext
                loadingData=true
                var list = postList.value
                var documents= withContext(Dispatchers.IO){repository.getPostsFrom(lastPost!!).await()}
                var listJobs= mutableListOf<Deferred<Boolean>>()
                documents.documents?.map{
                    listJobs.add(async {
                        var post =
                            withContext(Dispatchers.IO){repository.getPost(it.get("userRef") as String, it.get("postID") as String).await()}
                        var temPost = post.toObject(Post::class.java)
                        temPost?.liked = it.getBoolean("liked")
                        temPost?.id = it.id
                        temPost?.originalId = it.getString("postID")
                        temPost?.mediaUri = MutableList(temPost?.media?.size!!) { "".toUri() }
                        try {
                            temPost?.thumbUri = withContext(Dispatchers.IO){repository.getUrl(temPost?.thumb!!).await()}
                        }catch (e:Exception){
                            temPost?.thumbUri = null
                        }
                        var like =
                            withContext(Dispatchers.IO){repository.getLike(temPost.uid!!, temPost.originalId!!, auth.currentUser!!.uid)
                                .await()}
                        if (like.exists()) temPost.liked = true
                        var listMediaJobs = mutableListOf<Deferred<Unit>>()
                        temPost?.media?.map { path ->
                            listMediaJobs.add(async(Dispatchers.IO){
                                var position = temPost.media?.lastIndexOf(path)
                                try {
                                    temPost.mediaUri[position!!] = withContext(Dispatchers.IO){repository.getUrl(path).await()}
                                }catch (e:Exception){
                                    temPost.mediaUri[position!!] = "".toUri()
                                }
                            })
                        }
                        listMediaJobs.awaitAll()
                        list!!.add(temPost)
                    })
                }
                listJobs.awaitAll()
                if(documents.documents.size>0){
                    lastPost=documents.documents?.last()
                    withContext(Dispatchers.Main){postList.value=list}
                    loadingData=false
                }else{
                    withContext(Dispatchers.Main){postList.value=postList.value}
                }
            }
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
        removeAnimation(post)
    }

    private fun removeAnimation(post:Post){
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