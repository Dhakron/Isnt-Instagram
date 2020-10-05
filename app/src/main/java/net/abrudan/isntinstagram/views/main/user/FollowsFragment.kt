package net.abrudan.isntinstagram.views.main.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_follows.*

import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.FollowsTabAdapter


class FollowsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follows, container, false)
    }
    private lateinit var adapter: FollowsTabAdapter

    override fun onStart() {
        super.onStart()
        adapter = FollowsTabAdapter(this.activity!!)
        adapter.uid=arguments?.getString("uid")
        tabItemPager.adapter=null
        tabItemPager.adapter = adapter
        val tabLayoutMediator = TabLayoutMediator(tabLayout,tabItemPager,
            TabLayoutMediator.OnConfigureTabCallback { tab, position ->
                when(position){
                    0 -> {
                        tab.text = getString(R.string.tv_followers)
                    }
                    1 -> {
                        tab.text = getString(R.string.tv_following)
                    }
                }
            })
        tabLayoutMediator.attach()
        if(arguments?.getBoolean("followers")!!){
            tabLayout.selectTab(tabLayout.getTabAt(0))
            tabItemPager.setCurrentItem(0,false)
        }else{
            tabLayout.selectTab(tabLayout.getTabAt(1))
            tabItemPager.setCurrentItem(1,false)
        }
    }
}
