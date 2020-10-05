package net.abrudan.isntinstagram.views.camera

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.android.synthetic.main.fragment_profile_photo_set_view.*

import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.util.flip
import net.abrudan.isntinstagram.MainActivity
import java.io.ByteArrayOutputStream

private const val ARG_Image = "ARG_Image"
private const val ARG_Front = "ARG_Front"

class ProfilePhotoSet : Fragment() {
    private var upload:FirebaseStorage? = null
    private val auth = FirebaseAuth.getInstance()
    private var uri:Uri?= null
    private var front:Boolean? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_photo_set_view, container, false)
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser.let {
            upload = FirebaseStorage.getInstance()
        }
        arguments?.let {
            uri= it.getString(ARG_Image)?.toUri()
            front= it.getBoolean(ARG_Front)
        }
        btnAccept.setOnClickListener{
            onClickAccept()
        }
        btnCancel.setOnClickListener{
            onClickCancel()
        }
        front.let {
            if(front!!){ cropView.scaleX=-1.0F}
            Log.e("==============",front.toString())
        }
        cropView.of(uri)
            .withAspect(800, 800)
            .initialize(context)
    }

    private fun onClickAccept() {
        val bitMapFile= if(front!!)cropView.output.flip() else cropView.output
        var file = getImageUriFromBitmap(context!!,bitMapFile!!)
        var metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()
        var uploadTask = upload?.reference
            ?.child(auth.currentUser?.uid+"/profileImage.jpg")
            ?.putFile(file, metadata)
        uploadTask?.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            Log.e("==========================================","Upload is $progress% done")
        }?.addOnFailureListener {

        }?.addOnSuccessListener {
            val intent = Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }
    private fun onClickCancel() {

    }
}
