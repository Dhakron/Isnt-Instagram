package net.abrudan.isntinstagram.views.main.addPost

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_add_post.*
import kotlinx.android.synthetic.main.fragment_add_post.tabItemPager
import kotlinx.android.synthetic.main.fragment_add_post.tabLayout
import kotlinx.android.synthetic.main.fragment_follows.*

import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.AddPostTabAdapter

class AddPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_post, container, false)
    }
    private lateinit var adapter:AddPostTabAdapter
    override fun onStart() {
        super.onStart()
            adapter= AddPostTabAdapter(this.activity!!)
            if(tabItemPager.adapter==null)tabItemPager.adapter = adapter
            tabItemPager.isUserInputEnabled=false
            val tabLayoutMediator = TabLayoutMediator(tabLayout,tabItemPager,
            TabLayoutMediator.OnConfigureTabCallback { tab, position ->
                when(position){
                    0 -> {
                        tab.text = "Galeria"
                    }
                    1 -> {
                        tab.text = "Foto"
                    }
                    2 -> {
                        tab.text = "Video"
                    }
                }
            })
        tabLayout.selectTab(tabLayout.getTabAt(1))
        tabItemPager.setCurrentItem(1,false)
        tabLayoutMediator.attach()
    }
}
