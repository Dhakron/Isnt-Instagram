package net.abrudan.isntinstagram.adapters

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.fragment_upload_post.*
import net.abrudan.isntinstagram.model.MediaPost
import net.abrudan.isntinstagram.views.main.addPost.CropImageFragment

class UploadPostTabAdapter(fa: FragmentActivity, items:List<MediaPost>): FragmentStateAdapter(fa){
    private val itemsList=items
    override fun getItemCount(): Int = itemsList.size
    override fun createFragment(position: Int): Fragment {
        var fragment = CropImageFragment()
        var args = Bundle()
        args.putString("uri",itemsList[position].imgURI.toString())
        args.putBoolean("front",itemsList[position].front)
        args.putParcelable("mediaPost",itemsList.get(position))
        fragment.arguments = args
        return fragment
    }
}