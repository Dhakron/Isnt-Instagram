package net.abrudan.isntinstagram.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.abrudan.isntinstagram.model.HomeRepositories
import net.abrudan.isntinstagram.model.Post


class HomeViewModel (application: Application) : AndroidViewModel(application) {
    private val TAG = "Home-View-Model"
    private val postsRepositories=HomeRepositories(application)
    private var postList : MutableLiveData<List<Post?>> = MutableLiveData()

    fun getAllPosts():MutableLiveData<List<Post?>>{
        postsRepositories.getAllPosts().addOnCompleteListener {documents->
            var list= mutableListOf<Post?>()
            documents.result?.documents?.map {
                 postsRepositories.getPost(it.get("userRef") as String,it.get("postID") as String)
                    .addOnCompleteListener{post->
                        Log.e(TAG,"He conseguido un documento! Home")
                        list.add(post.result?.toObject(Post::class.java))
                        postList.value=list
                }
            }
        }.addOnFailureListener{
            Log.e(TAG,"Error consiguiendo POSTS")
        }
        return postList
    }
}