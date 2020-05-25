package net.abrudan.isntinstagram.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import net.abrudan.isntinstagram.model.Repositories
import net.abrudan.isntinstagram.model.UserInfo

class SearchViewModel : ViewModel(){

    private val TAG = "Search-View-Model"
    private val searchRepositories= Repositories()
    private var searchList : MutableLiveData<List<UserInfo?>> = MutableLiveData()
    private var userList : MutableLiveData<List<UserInfo?>> = MutableLiveData()
    private var lastUser:DocumentSnapshot?=null
    private var lastTextSearch=""
    var loadingData=false
    private val auth=FirebaseAuth.getInstance()
    fun getUsers()=userList
    fun getSearchUsers()=searchList
    fun searchUser(userID:String){
        searchList.value= emptyList()
        searchRepositories.searchUser(userID).addOnCompleteListener {documents->
            var list= mutableListOf<UserInfo?>()
            documents.result?.documents?.map {
                var user=it.toObject(UserInfo::class.java)
                if(user?.userUID==auth.currentUser?.uid)return@map
                searchRepositories.getUrl(user?.imageProfile?:"").addOnSuccessListener{uri->
                    user?.profileImgURI=uri
                    list.add(user)
                }.addOnFailureListener{
                    user?.profileImgURI=null
                    list.add(user)
                }.continueWith{
                    searchList.value=list
                    lastUser=documents.result?.documents?.last()
                    lastTextSearch=userID
                    return@continueWith
                }
            }
            if(documents.result!!.documents.size>0){
                lastUser=documents.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= searchList.value
                searchList.value=tempList
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo POSTS")
        }
    }
    fun searchUserFrom(){
        if(lastUser==null)return
        searchRepositories.searchUserFrom(lastTextSearch,lastUser!!).addOnCompleteListener {documents->
            var list= searchList.value!!.toMutableList()
            documents.result?.documents?.map {
                var user=it.toObject(UserInfo::class.java)
                if(user?.userUID==auth.currentUser?.uid)return@map
                searchRepositories.getUrl(user?.imageProfile?:"").addOnSuccessListener{uri->
                    user?.profileImgURI=uri
                    list.add(user)
                }.addOnFailureListener{
                    user?.profileImgURI=null
                    list.add(user)
                }.continueWith{
                    searchList.value=list
                    return@continueWith
                }
            }
            if(documents.result!!.documents.size>0){
                lastUser=documents.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= searchList.value
                searchList.value=tempList
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo POSTS")
        }
    }
    fun loadAllUsers(){
        loadingData=true
        userList.value= emptyList()
        searchRepositories.getAllUserInfo().addOnCompleteListener {documents->
            var list= mutableListOf<UserInfo?>()
            documents.result?.documents?.map {
                var user=it.toObject(UserInfo::class.java)
                if(user?.userUID==auth.currentUser?.uid)return@map
                searchRepositories.getUrl(user?.imageProfile?:"").addOnSuccessListener{uri->
                    user?.profileImgURI=uri
                }.addOnFailureListener {
                }.continueWith{
                    user?.followYou=true
                    list.add(user)
                    userList.value=list
                    return@continueWith
                }
            }
            if(documents.result!!.documents.size>0){
                lastUser=documents.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= userList.value
                userList.value=tempList
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo Usuarios")
        }
    }
    fun loadAllUsersFrom(){
        if(loadingData||lastUser==null)return
        loadingData=true
        searchRepositories.getAllUserInfoFrom(lastUser!!).addOnCompleteListener {documents->
            var list= userList.value!!.toMutableList()
            documents.result?.documents?.map {
                var user=it.toObject(UserInfo::class.java)
                if(user?.userUID==auth.currentUser?.uid)return@map
                searchRepositories.getUrl(user?.imageProfile?:"").addOnSuccessListener{uri->
                    user?.profileImgURI=uri
                }.continueWith{
                    user?.followYou=true
                    list.add(user)
                    userList.value=list
                    return@continueWith
                }
            }
            if(documents.result!!.documents.size>0){
                lastUser=documents.result?.documents?.last()
                loadingData=false
            }else{
                val tempList= userList.value
                userList.value=tempList
            }
        }.addOnFailureListener{
        }
    }
}