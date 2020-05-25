package net.abrudan.isntinstagram.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.awaitAll
import net.abrudan.isntinstagram.model.Repositories
import net.abrudan.isntinstagram.model.UserInfo
import net.abrudan.isntinstagram.model.activities

class FollowsViewModel () : ViewModel() {
    private val TAG = "Follows-View-Model"
    private val userRepositories= Repositories()
    var followersList : MutableLiveData<List<UserInfo?>> = MutableLiveData()
    var followingsList : MutableLiveData<List<UserInfo?>> = MutableLiveData()
    private var lastPost:DocumentSnapshot?=null
    private var auth=FirebaseAuth.getInstance()
    var isFollowing=false
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
                lastPost=documents.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= followersList.value
                followersList.value=tempList
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo Followers")
        }
    }
    fun loadingData()=loadingData
    fun getAllFollowers():MutableLiveData<List<UserInfo?>>{
        return followersList
    }
    fun loadAllFollowing(uid: String){
        loadingData=true
        followingsList.value= emptyList()
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
                        followingsList.value=list
                        return@continueWith
                    }
            }
            if(documents.result!!.documents.size>0){
                lastPost=documents.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= followingsList.value
                followingsList.value=tempList
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo Followings")
        }
    }
    fun loadAllFollowingFrom(uid: String){
        if(loadingData||lastPost==null)return
        loadingData=true
        userRepositories.getAllFollowsFrom(uid,lastPost!!).addOnCompleteListener {documents->
            var list= followingsList.value!!.toMutableList()
            documents.result?.documents?.map {
                var user=it.toObject(UserInfo::class.java)
                userRepositories.getUrl(user?.imageProfile?:"").addOnSuccessListener{uri->
                    user?.profileImgURI=uri
                }.continueWith{
                    user?.followYou=true
                    list.add(user)
                    followingsList.value=list
                    return@continueWith
                }
            }
            if(documents.result!!.documents.size>0){
                lastPost=documents.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= followingsList.value
                followingsList.value=tempList
            }
        }.addOnFailureListener{
        }
    }
    fun loadAllFollowersFrom(uid: String) {
        if (loadingData || lastPost == null) return
        loadingData = true
        userRepositories.getAllFollowersFrom(uid, lastPost!!).addOnCompleteListener { documents ->
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
                lastPost = documents.result?.documents?.last()
                loadingData = false
            } else {
                val tempList = followersList.value
                followersList.value = tempList
            }
        }
    }
    fun getAllFollowing():MutableLiveData<List<UserInfo?>>{
        return followingsList
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