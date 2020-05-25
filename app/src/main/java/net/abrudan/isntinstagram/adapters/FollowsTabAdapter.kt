package net.abrudan.isntinstagram.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.abrudan.isntinstagram.views.main.user.FollowsTabItemFragment


class FollowsTabAdapter(fa:FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2
    var uid:String? = null
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                var fragment =
                    FollowsTabItemFragment()
                var args = Bundle()
                args.putString("uid",uid)
                args.putBoolean("followers",true)
                fragment.arguments = args
                fragment
            }
            else -> {
                var fragment =
                    FollowsTabItemFragment()
                var args = Bundle()
                args.putString("uid",uid)
                args.putBoolean("followers",false)
                fragment.arguments = args
                fragment
            }
        }
    }
}