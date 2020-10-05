package net.abrudan.isntinstagram.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.request.LoadRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.tasks.await
import net.abrudan.isntinstagram.model.Post
import net.abrudan.isntinstagram.model.Repositories
import net.abrudan.isntinstagram.model.UserInfo
import kotlin.Exception

class FollowsViewModel (application: Application) : AndroidViewModel(application) {
    private val TAG = "Follows-View-Model"
    private val userRepositories= Repositories()
    var followersList : MutableLiveData<List<UserInfo?>> = MutableLiveData()
    var followingList : MutableLiveData<List<UserInfo?>> = MutableLiveData()
    private var lastFollower:DocumentSnapshot?=null
    private var lastFollowing:DocumentSnapshot?=null
    private var auth=FirebaseAuth.getInstance()
    private var context=application
    private var imageLoader= Coil.imageLoader(application)
    var loadingData=false

    fun loadAllFollowers(uid:String){
        loadingData=true
        followersList.value= emptyList()
        userRepositories.getAllFollowers(uid).addOnCompleteListener {documents->
            var list= mutableListOf<UserInfo?>()
            documents.result?.documents?.map {
                var user=it.toObject(UserInfo::class.java)
                userRepositories.getUrl(user?.imageProfile?:"").addOnSuccessListener{uri->
                    user?.profileImgURI=uri
                }.continueWith{
                    userRepositories.getFollower(auth.currentUser!!.uid, user?.userUID!!).addOnSuccessListener { doc ->
                        if (doc.exists())user?.followYou = true
                    }.continueWith{
                        list.add(user)
                        followersList.value = list
                    }
                }
            }
            if(documents.result!!.documents.size>0){
                lastFollower=documents.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= followersList.value
                followersList.value=tempList
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo Followers")
        }
    }
    fun loadAllFollowersSync(uid:String){
        viewModelScope.launch {
                loadingData=true
                val documents= withContext(Dispatchers.IO){userRepositories.getAllFollowers(uid).await()}
                val listJobs= mutableListOf<Deferred<Boolean>>()
                var list= mutableListOf<UserInfo>()
                documents.documents.map {
                    listJobs.add(async {
                        var user=it.toObject(UserInfo::class.java)
                        try {
                            user?.profileImgURI= withContext(Dispatchers.IO){userRepositories.getUrl(user?.imageProfile?:"").await()}
                        }catch (e:Exception){
                            user?.profileImgURI=null
                        }
                        val request = LoadRequest.Builder(context)
                            .data(user?.imageProfile)
                            .build()
                        imageLoader.execute(request).await()
                        val follow= withContext(Dispatchers.IO){userRepositories.getFollower(auth.currentUser!!.uid, user?.userUID!!).await()}
                        if(follow.exists())user?.followYou = true
                        list.add(user!!)
                    })
                }
                listJobs.awaitAll()
                if(documents.documents.size>0){
                    lastFollower=documents.documents?.last()
                    loadingData=false
                    followersList.value=list
                }else{
                    followersList.value=followersList.value
                }
            }
    }

    fun loadingData()=loadingData
    fun getAllFollowers():MutableLiveData<List<UserInfo?>>{
        return followersList
    }

    fun loadAllFollowing(uid: String){
        loadingData=true
        followingList.value= emptyList()
        userRepositories.getAllFollows(uid).addOnCompleteListener {documents->
            var list= mutableListOf<UserInfo?>()
            documents.result?.documents?.map {
                var user=it.toObject(UserInfo::class.java)
                    userRepositories.getUrl(user?.imageProfile?:"").addOnSuccessListener{uri->
                        user?.profileImgURI=uri
                    }.addOnFailureListener {
                    }.continueWith{
                        user?.followYou=true
                        list.add(user)
                        followingList.value=list
                        return@continueWith
                    }
            }
            if(documents.result!!.documents.size>0){
                lastFollowing=documents.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= followingList.value
                followingList.value=tempList
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo Followings")
        }
    }
    fun loadAllFollowingSync(uid: String) {
        viewModelScope.launch {
                loadingData = true
                val documents =
                    withContext(Dispatchers.IO) { userRepositories.getAllFollows(uid).await() }
                var listJobs = mutableListOf<Deferred<Boolean>>()
                var list = mutableListOf<UserInfo>()
                documents.documents.map {
                    listJobs.add(async {
                        var user = it.toObject(UserInfo::class.java)!!
                        try {
                            user?.profileImgURI= withContext(Dispatchers.IO){userRepositories.getUrl(user?.imageProfile?:"").await()}
                        }catch (e:Exception){
                            user?.profileImgURI=null
                        }
                        val follow = withContext(Dispatchers.IO) {
                            userRepositories.getFollower(
                                auth.currentUser!!.uid,
                                user?.userUID!!
                            ).await()
                        }
                        if (follow.exists()) user?.followYou = true
                        list.add(user)
                    })
                }
                listJobs.awaitAll()
                if (documents.documents.size > 0) {
                    lastFollowing = documents.documents?.last()
                    followingList.value = list
                    loadingData = false
                } else {
                    followingList.value = followingList.value
                }
            }
    }

    fun loadAllFollowingFrom(uid: String){
        if(loadingData||lastFollowing==null)return
        loadingData=true
        userRepositories.getAllFollowsFrom(uid,lastFollowing!!).addOnCompleteListener {documents->
            var list= followingList.value!!.toMutableList()
            documents.result?.documents?.map {
                var user=it.toObject(UserInfo::class.java)
                userRepositories.getUrl(user?.imageProfile?:"").addOnSuccessListener{uri->
                    user?.profileImgURI=uri
                }.continueWith{
                    user?.followYou=true
                    list.add(user)
                    followingList.value=list
                    return@continueWith
                }
            }
            if(documents.result!!.documents.size>0){
                lastFollowing=documents.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= followingList.value
                followingList.value=tempList
            }
        }.addOnFailureListener{
        }
    }
    fun loadAllFollowingFromSync(uid: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (loadingData || lastFollowing == null) return@withContext
                loadingData = true
                val documents = withContext(Dispatchers.IO) {
                    userRepositories.getAllFollowsFrom(
                        uid,
                        lastFollowing!!
                    ).await()
                }
                var list = followingList.value?.toMutableList()
                var listJobs = mutableListOf<Deferred<Boolean>>()
                documents.documents.map {
                    listJobs.add(async {
                        var user = it.toObject(UserInfo::class.java)
                        try {
                            user?.profileImgURI= withContext(Dispatchers.IO){userRepositories.getUrl(user?.imageProfile?:"").await()}
                        }catch (e:Exception){
                            user?.profileImgURI=null
                        }
                        val follow = withContext(Dispatchers.IO) {
                            userRepositories.getFollower(
                                auth.currentUser!!.uid,
                                user?.userUID!!
                            ).await()
                        }
                        if (follow.exists()) user?.followYou = true
                        list!!.add(user!!)
                    })
                }
                listJobs.awaitAll()
                if (documents.documents.size > 0) {
                    lastFollowing = documents.documents?.last()
                    loadingData = false
                    withContext(Dispatchers.Main){followingList.value = list}
                } else {
                    withContext(Dispatchers.Main){followingList.value = followingList.value}
                }
            }
        }
    }

    fun loadAllFollowersFrom(uid: String) {
        if (loadingData || lastFollower == null) return
        loadingData = true
        userRepositories.getAllFollowersFrom(uid, lastFollower!!).addOnCompleteListener { documents ->
            var list = followersList.value!!.toMutableList()
            documents.result?.documents?.map {
                var user = it.toObject(UserInfo::class.java)
                userRepositories.getUrl(user?.imageProfile ?: "").addOnSuccessListener { uri ->
                    user?.profileImgURI = uri
                }.continueWith {
                    userRepositories.getFollower(uid, user?.userUID!!).addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            user?.followYou = true
                        }
                        list.add(user)
                        followersList.value = list
                    }
                }
            }
            if (documents.result!!.documents.size > 0) {
                lastFollower = documents.result?.documents?.last()
                loadingData = false
            } else {
                val tempList = followersList.value
                followersList.value = tempList
            }
        }
    }

    fun loadAllFollowersFromSync(uid: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (loadingData || lastFollower == null) return@withContext
                loadingData = true
                val documents = withContext(Dispatchers.IO) {
                    userRepositories.getAllFollowersFrom(
                        uid,
                        lastFollower!!
                    ).await()
                }
                var list = followersList.value?.toMutableList()
                var listJobs = mutableListOf<Deferred<Boolean>>()
                documents.documents.map {
                    listJobs.add(async {
                        var user = it.toObject(UserInfo::class.java)
                        try {
                            user?.profileImgURI= withContext(Dispatchers.IO){userRepositories.getUrl(user?.imageProfile?:"").await()}
                        }catch (e:Exception){
                            user?.profileImgURI=null
                        }
                        val follow = withContext(Dispatchers.IO) {
                            userRepositories.getFollower(
                                auth.currentUser!!.uid,
                                user?.userUID!!
                            ).await()
                        }
                        if (follow.exists()) user?.followYou = true
                        list!!.add(user!!)
                    })
                }
                listJobs.awaitAll()
                if (documents.documents.size > 0) {
                    lastFollower = documents.documents?.last()
                    loadingData = false
                    withContext(Dispatchers.Main){followersList.value = list}
                } else {
                    withContext(Dispatchers.Main){followersList.value = followersList.value}
                }
            }
        }
    }

    fun getAllFollowing():MutableLiveData<List<UserInfo?>>{
        return followingList
    }
    fun follow(uid:String){
        userRepositories.follow(uid).addOnCompleteListener{
            if(it.isSuccessful){
                Log.e(TAG,"Ahora le sigues")
            }else{
                Log.e(TAG,"Error, ya le seguias")
            }
        }
    }
    fun unFollow(uid:String){
        userRepositories.unFollow(uid).addOnCompleteListener{
            if(it.isSuccessful){
                Log.e(TAG,"Ahora no le sigues")
            }else{
                Log.e(TAG,"Error, no le seguias")
            }
        }
    }
}