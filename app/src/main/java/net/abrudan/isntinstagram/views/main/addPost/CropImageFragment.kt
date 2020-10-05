package net.abrudan.isntinstagram.views.main.addPost

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_crop_image.*
import kotlinx.android.synthetic.main.fragment_upload_post.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.MediaPost


/**
 * A simple [Fragment] subclass.
 */
class CropImageFragment : Fragment() {
    private var front:Boolean? = null
    private var uri: Uri?=null
    private var mediaPost: MediaPost?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_crop_image, container, false)
    }

    override fun onStart() {
        super.onStart()
        arguments?.let {
            uri= it.getString("uri")?.toUri()
            front= it.getBoolean("front")
            mediaPost=it.getParcelable("mediaPost")
        }
        Glide.with(context!!).asBitmap().load(mediaPost!!.bitMap).into(imageView2)
    }
}
