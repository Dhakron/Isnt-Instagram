package net.abrudan.isntinstagram.views.main

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_crop_image.*
import kotlinx.android.synthetic.main.fragment_row_media_item_view.*

import net.abrudan.isntinstagram.R

/**
 * A simple [Fragment] subclass.
 */
class RowMediaItemView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_row_media_item_view, container, false)
    }
    private lateinit var uri: Uri
    override fun onStart() {
        super.onStart()
        arguments?.let {
            uri= it.getString("uri")?.toUri()!!
        }
        Log.e("SAAAAAAAAASSSSSSSSSSSSSSSS","Quiero pintarrr"+uri)
        Picasso.get().load(uri).into(ivMedia2)
    }
}
