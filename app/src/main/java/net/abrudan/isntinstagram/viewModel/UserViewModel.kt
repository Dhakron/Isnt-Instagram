package net.abrudan.isntinstagram.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.model.value.IntegerValue
import net.abrudan.isntinstagram.model.Post
import net.abrudan.isntinstagram.model.User
import net.abrudan.isntinstagram.model.UserRepositories

class UserViewModel : ViewModel() {
    private val TAG = "User-View-Model"
    private val userRepositories=UserRepositories()
    private var postList : MutableLiveData<List<Post?>> = MutableLiveData()
    private var auth= FirebaseAuth.getInstance()
    private var userData:MutableLiveData<User> = MutableLiveData()
    fun getUserData():MutableLiveData<User>{
        userRepositories.getUserData(auth.uid?:"").addOnCompleteListener{data->
            var user=data.result?.toObject(User::class.java)?:User()
            user.uid=(data.result?.id?:"0")
            Log.e(TAG,user.uid?:"")
            userData.value=user
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo User")
            userData.value= User()
        }
        return userData
    }
    fun getUID():String{
        return auth.uid!!
    }
    fun getAllPosts():MutableLiveData<List<Post?>>{
        userRepositories.getAllUserPosts(auth.uid?:"").addOnCompleteListener {documents->
            var list= mutableListOf<Post?>()
            documents.result?.documents?.map {
                Log.e(TAG,"He conseguido un documento!")
                list.add(it.toObject(Post::class.java))
            }
            postList.value=list
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo POSTS")
        }
        return postList
    }

}