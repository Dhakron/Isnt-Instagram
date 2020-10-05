package net.abrudan.isntinstagram.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.abrudan.isntinstagram.views.main.addPost.GalleryFragment
import net.abrudan.isntinstagram.views.main.addPost.PhotoFragment
import net.abrudan.isntinstagram.views.main.addPost.VideoFragment

class AddPostTabAdapter(fa:FragmentActivity): FragmentStateAdapter(fa){
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                GalleryFragment()
            }
            1 -> {
                PhotoFragment()
            }
            else -> {
                VideoFragment()
            }
        }
    }
}