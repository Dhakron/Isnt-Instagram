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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import net.abrudan.isntinstagram.MainActivity
import net.abrudan.isntinstagram.model.Comment
import net.abrudan.isntinstagram.model.Post
import net.abrudan.isntinstagram.model.Repositories
import org.jetbrains.anko.doAsync
import java.lang.Exception
import java.util.*


class CommentsViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "Comments-View-Model"
    private val repository= Repositories()
    private var commentsList : MutableLiveData<MutableList<Comment?>> = MutableLiveData()
    private var lastComment:DocumentSnapshot?=null
    private var repliesList : MutableLiveData<MutableList<Comment?>> = MutableLiveData()
    private var lastreply:DocumentSnapshot?=null
    private var loadingData=false
    private var loadingReplies=false
    private var storage= FirebaseStorage.getInstance()
    private var auth= FirebaseAuth.getInstance()
    private var context=application
    private var imageLoader= Coil.imageLoader(application)


    fun getAllComments():MutableLiveData<MutableList<Comment?>>{
        return commentsList
    }

    fun loadAllCommentsSync(postRef:String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                var list = mutableListOf<Comment?>()
                loadingData = true
                var documents =  withContext(Dispatchers.IO){repository.getComments(postRef).await()}
                var listJobs= mutableListOf<Deferred<Boolean>>()
                documents.documents.map {
                    listJobs.add(async {
                        var comment = it.toObject(Comment::class.java)
                        val commentRef=comment?.postRef+"/Comments/"+it.id
                        comment?.commentRef= commentRef
                        comment?.profileImgURI = withContext(Dispatchers.IO){repository.getUrl(comment?.thumb!!).await()}
                        val request = LoadRequest.Builder(context)
                            .data(comment?.profileImgURI)
                            .build()
                        imageLoader.execute(request).await()
                        var like =
                            withContext(Dispatchers.IO){repository.getLikeComment(commentRef, auth.currentUser!!.uid)
                                .await()}
                        if (like.exists()) comment?.liked = true
                        if(comment?.replies!! in 1..3){
                            var replies =  withContext(Dispatchers.IO){repository.getReplies(comment.postRef!!+"/Comments/"+it.id).await()}
                            var listRepliesJobs = mutableListOf<Deferred<Boolean>>()
                            replies.documents.map {
                                listRepliesJobs.add(async {
                                    var reply = it.toObject(Comment::class.java)
                                    reply?.commentRef= commentRef
                                    reply?.replyRef=comment.commentRef!!+"/Replies/"+it.id
                                    reply?.isReply=true
                                    reply?.profileImgURI = withContext(Dispatchers.IO){repository.getUrl(reply?.thumb!!).await()}
                                    var like =
                                        withContext(Dispatchers.IO){repository.getLikeCommentReply(commentRef,it.id, auth.currentUser!!.uid)
                                            .await()}
                                    if (like.exists()) reply?.liked = true
                                    comment?.repliesList!!.add(reply!!)
                                })
                            }
                            listRepliesJobs.awaitAll()
                            comment?.repliesList!!.sortBy{ it!!.date }
                            comment?.viewReplies=true
                            comment.lastReply=replies.documents.last()
                        }
                        list.add(comment)
                    })
                }
                listJobs.awaitAll()
                if (documents.documents.size > 0) {
                    withContext(Dispatchers.Main) {commentsList.value=list}
                    lastComment = documents.documents?.last()
                    loadingData = false
                } else {
                    withContext(Dispatchers.Main) {
                        commentsList.value = emptyList<Comment?>().toMutableList()
                    }
                }
            }
        }
    }

    fun loadingData():Boolean{
        return loadingData
    }

    fun loadAllCommentsFromLastSync(postRef: String){
        viewModelScope.launch {
            withContext(Dispatchers.Default){
                if(loadingData||lastComment==null)return@withContext
                loadingData=true
                var list = commentsList.value
                var documents= withContext(Dispatchers.IO){repository.getCommentsFrom(postRef,lastComment!!).await()}
                var listJobs= mutableListOf<Deferred<Boolean>>()
                documents.documents?.map{
                    listJobs.add(async {
                        var comment = it.toObject(Comment::class.java)
                        val commentRef=comment?.postRef+"/Comments/"+it.id
                        comment?.commentRef= commentRef
                        comment?.profileImgURI = withContext(Dispatchers.IO){repository.getUrl(comment?.thumb!!).await()}
                        val request = LoadRequest.Builder(context)
                            .data(comment?.profileImgURI)
                            .build()
                        imageLoader.execute(request).await()
                        var like =
                            withContext(Dispatchers.IO){repository.getLikeComment(commentRef, auth.currentUser!!.uid)
                                .await()}
                        if (like.exists()) comment?.liked = true
                        if(comment?.replies!! in 1..3){
                            var replies =  withContext(Dispatchers.IO){repository.getReplies(comment.postRef!!+"/"+it.id).await()}
                            var listRepliesJobs = mutableListOf<Deferred<Boolean>>()
                            replies.documents.map {
                                listRepliesJobs.add(async {
                                    var reply = it.toObject(Comment::class.java)
                                    reply?.commentRef=commentRef
                                    reply?.replyRef=comment.commentRef!!+"/Replies/"+it.id
                                    reply?.isReply=true
                                    reply?.profileImgURI = withContext(Dispatchers.IO){repository.getUrl(reply?.thumb!!).await()}
                                    var like =
                                        withContext(Dispatchers.IO){repository.getLikeCommentReply(commentRef,it.id, auth.currentUser!!.uid)
                                            .await()}
                                    if (like.exists()) reply?.liked = true
                                    comment?.repliesList!!.add(reply!!)
                                })
                            }
                            listRepliesJobs.awaitAll()
                            comment?.repliesList!!.sortBy{ it!!.date }
                            comment.viewReplies=true
                            comment.lastReply=replies.documents.last()
                        }
                        list!!.add(comment)
                    })
                }
                listJobs.awaitAll()
                if(documents.documents.size>0){
                    lastComment=documents.documents?.last()
                    withContext(Dispatchers.Main){commentsList.value=list}
                    loadingData=false
                }else{
                    withContext(Dispatchers.Main){commentsList.value=commentsList.value}
                }
            }
        }
    }

    fun loadReplies(comment: Comment){
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (loadingReplies) return@withContext
                loadingReplies = true
                var tempComment=comment
                var list = commentsList.value
                list?.remove(tempComment)
                var documents =
                    withContext(Dispatchers.IO) { repository.getReplies(comment.commentRef!!).await() }
                var listJobs = mutableListOf<Deferred<Boolean>>()
                documents.documents?.map {
                    listJobs.add(async {
                        var reply = it.toObject(Comment::class.java)
                        reply?.commentRef = comment.commentRef!!
                        reply?.replyRef=comment.commentRef!!+"/Replies/"+it.id
                        reply?.isReply = true
                        reply?.profileImgURI = withContext(Dispatchers.IO) {
                            repository.getUrl(reply?.thumb!!).await()
                        }
                        var like =
                            withContext(Dispatchers.IO){repository.getLikeCommentReply(tempComment.commentRef!!,it.id, auth.currentUser!!.uid)
                                .await()}
                        if (like.exists()) reply?.liked = true
                        tempComment.repliesList!!.add(reply!!)
                    })
                }
                listJobs.awaitAll()
                tempComment?.repliesList!!.sortBy{ it!!.date }
                tempComment.viewReplies=true
                tempComment.loadingReply=false
                tempComment.lastReply=documents.documents.last()
                list?.add(tempComment)
                withContext(Dispatchers.Main){
                    commentsList.value=list
                }
                loadingReplies=false
            }
        }
    }

    fun loadRepliesFrom(comment: Comment){
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (loadingReplies) return@withContext
                loadingReplies = true
                var tempComment=comment
                var list = commentsList.value
                list?.remove(tempComment)
                var documents =
                    withContext(Dispatchers.IO) { repository.getRepliesFrom(comment.commentRef!!,comment.lastReply!!).await() }
                var listJobs = mutableListOf<Deferred<Unit>>()
                documents.documents?.map {
                    listJobs.add(async {
                        var reply = it.toObject(Comment::class.java)
                        reply?.commentRef = comment.commentRef!!
                        reply?.replyRef=comment.commentRef!!+"/Replies/"+it.id
                        reply?.isReply = true
                        reply?.profileImgURI = withContext(Dispatchers.IO) {
                            repository.getUrl(reply?.thumb!!).await()
                        }
                        var like =
                            withContext(Dispatchers.IO){repository.getLikeCommentReply(tempComment.commentRef!!,it.id, auth.currentUser!!.uid)
                                .await()}
                        if (like.exists()) reply?.liked = true
                        comment.repliesList!!.add(reply!!)
                        val request = LoadRequest.Builder(context)
                            .data(reply?.profileImgURI)
                            .build()
                        imageLoader.execute(request).await()
                    })
                }
                listJobs.awaitAll()
                comment?.repliesList!!.sortBy{ it!!.date }
                comment.viewReplies=true
                comment.loadingReply=false
                comment.lastReply=documents.documents.last()
                list?.add(tempComment)
                withContext(Dispatchers.Main){
                    commentsList.value=list
                }
                loadingReplies=false
            }
        }
    }

    fun addComment(comment:String ,postRef: String, replyTo:String?): Task<HttpsCallableResult> {
        return repository.addComment(comment, postRef, replyTo)
    }
    fun likeComment(commentRef:String): Task<HttpsCallableResult> {
        return repository.likeComment(commentRef)
    }
    fun unLikeComment(commentRef:String): Task<HttpsCallableResult> {
        return repository.unlikeComment(commentRef)
    }
}