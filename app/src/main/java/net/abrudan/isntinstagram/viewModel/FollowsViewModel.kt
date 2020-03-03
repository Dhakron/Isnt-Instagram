package net.abrudan.isntinstagram.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.awaitAll
import net.abrudan.isntinstagram.model.UserInfo
import net.abrudan.isntinstagram.model.UserRepositories

class FollowsViewModel (application: Application) : AndroidViewModel(application) {
    private val TAG = "Follows-View-Model"
    private val userRepositories=UserRepositories()
    private var followersList : MutableLiveData<List<UserInfo?>> = MutableLiveData()

    fun getAllFollowers(uid:String):MutableLiveData<List<UserInfo?>>{
        userRepositories.getAllFollowers(uid).addOnCompleteListener {documents->
            var list= mutableListOf<UserInfo?>()
            documents.result?.documents?.map {
                var user=it.toObject(UserInfo::class.java)
                userRepositories.getUrl(user?.profileImg?:"").addOnCompleteListener{uri->
                    user?.profileImgURI=uri.result
                    Log.e(TAG,user?.profileImgURI.toString())
                    list.add(user)
                }.addOnFailureListener {
                    Log.e(TAG,user?.profileImgURI.toString())
                }.continueWith{
                    followersList.value=list
                    return@continueWith
                }
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo Followers")
        }
        return followersList
    }
    fun getAllFollows(uid:String):MutableLiveData<List<UserInfo?>>{
        userRepositories.getAllFollows(uid).addOnCompleteListener {documents->
            var list= mutableListOf<UserInfo?>()
            documents.result?.documents?.map {
                var user=it.toObject(UserInfo::class.java)
                userRepositories.getUrl(user?.profileImg?:"").addOnCompleteListener{uri->
                    user?.profileImgURI=uri.result
                    Log.e(TAG,user?.profileImgURI.toString())
                    list.add(user)
                }.addOnFailureListener {
                    Log.e(TAG,user?.profileImgURI.toString())
                }.continueWith{
                    followersList.value=list
                    return@continueWith
                }
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo Followers")
        }
        return followersList
    }
}