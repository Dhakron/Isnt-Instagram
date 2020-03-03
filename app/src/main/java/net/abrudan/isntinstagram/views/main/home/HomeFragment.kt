package net.abrudan.isntinstagram.views.main.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.HomeAdapter
import net.abrudan.isntinstagram.viewModel.HomeViewModel


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var  adapter: HomeAdapter

    override fun onStart() {
        super.onStart()
        initRV()
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.getAllPosts().observe(this, Observer { postView->
            postView?.let {
                adapter.setPost(it)
            } })
    }
    private fun initRV() {
        adapter= HomeAdapter(context!!,R.layout.row_post)
        rvHome.adapter=adapter
        rvHome.layoutManager= LinearLayoutManager(context)
    }
}