package net.abrudan.isntinstagram.viewModel

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import net.abrudan.isntinstagram.model.*
import java.io.ByteArrayOutputStream
import java.util.*

class GlobalViewModel: ViewModel() {
    val TAG = "---GlobalViewModel---"
    val repositories=Repositories()
    val userAuth=FirebaseAuth.getInstance()
    private var lastPost:MutableLiveData<List<Post?>> = MutableLiveData()
    private var myUserData : MutableLiveData<User?> = MutableLiveData()
    private var uploadItemsCount=0
    private var uploadingPost:MutableLiveData<Boolean?> = MutableLiveData()
    private var upload: FirebaseStorage? = FirebaseStorage.getInstance()
    private var uploadPathList= arrayListOf<String>()
    fun getMyUserData()=myUserData
    fun addPost(tittle: String, media: List<String>): Task<HttpsCallableResult> {
        return repositories.addPost(tittle, media)
    }
    fun getUploadingPost()=uploadingPost
    fun loadMyuserData(){
        if(userAuth.currentUser!=null){
            repositories.getUserData(userAuth.currentUser!!.uid).addOnSuccessListener {
                val userData=it.toObject(User::class.java)
                repositories.getUrl(userData?.imageProfile!!).addOnSuccessListener {uri->
                    myUserData.value?.profileImgURI=uri
                }
                myUserData.value=userData
                Log.e(TAG,myUserData.value.toString())
            }.addOnFailureListener {
                Log.e(TAG,"Error cargado los datos de usuario")
            }
        }else Log.e(TAG,"Error, usuario NULL")
    }
    fun chekAllUpload(tittle: String) {
        if (uploadItemsCount > 0) return
        addPost(tittle, uploadPathList).addOnSuccessListener {
            uploadPathList.clear()
            uploadingPost.value=false
        }.addOnFailureListener {
            uploadPathList.clear()
            uploadingPost.value=null
        }

    }
    fun saveLastPosts(list:List<Post?>){
        if(list.size>10)return
        lastPost.value= emptyList()
        lastPost.value=list
    }

    fun uploadMedia(listData:List<MediaPost>,tittle: String) {
        uploadingPost.value=true
        listData.forEach {
            val refPath = userAuth.currentUser?.uid + "/Posts/" + Date().time + ".jpg"
            val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build()
            val baos = ByteArrayOutputStream()
            it.bitMap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val currentItem = uploadItemsCount
            uploadItemsCount++
            upload?.reference
                ?.child(refPath)
                ?.putBytes(data, metadata)?.addOnFailureListener {
                    Log.e("SSSSSSSSSSSSSSSSSSSSS", "ERROR UPLOADING IMAGE" + currentItem)
                    uploadItemsCount--
                }?.addOnSuccessListener {
                    Log.e("SSSSSSSSSSSSSSSSSSSSS", "UPLOADED IMAGE" + currentItem)
                    uploadItemsCount--
                    uploadPathList.add(refPath)
                    chekAllUpload(tittle)
                }
        }
    }

}