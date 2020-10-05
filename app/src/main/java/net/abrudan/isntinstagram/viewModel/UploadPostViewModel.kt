package net.abrudan.isntinstagram.viewModel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import net.abrudan.isntinstagram.model.MediaPost
import net.abrudan.isntinstagram.model.Repositories
import java.io.ByteArrayOutputStream
import java.util.*

class UploadPostViewModel : ViewModel(){
    private val TAG = "UploadPost-View-Model"
    private val repositorie= Repositories()
    private var upload: FirebaseStorage? = FirebaseStorage.getInstance()
    private var auth=FirebaseAuth.getInstance()
    private var uploadProgress:Int=0
    private var uploadItemsProgress= mutableListOf<Double>()
    private var uploadItemsCount=0
    private var uploadPathList= arrayListOf<String>()

    fun addPost(tittle:String,media:List<String>): Task<HttpsCallableResult> {
        return repositorie.addPost(tittle,media)
    }
    fun uploadProgress(){
    }



}