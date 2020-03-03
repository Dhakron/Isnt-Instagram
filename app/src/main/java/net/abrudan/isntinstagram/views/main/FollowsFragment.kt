package net.abrudan.isntinstagram.views.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_follows.*

import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.FollowsAdapter
import net.abrudan.isntinstagram.adapters.UserAdapter
import net.abrudan.isntinstagram.viewModel.FollowsViewModel



class FollowsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follows, container, false)
    }

    private lateinit var followsViewModel: FollowsViewModel
    private lateinit var adapter: FollowsAdapter
    private var uid:String?=null
    private var followers:Boolean=false
    override fun onStart() {
        super.onStart()
        initRV()
        followers= arguments?.getBoolean("followers")?:false
        uid = arguments?.getString("uid")?:""
        followsViewModel = ViewModelProvider(this).get(FollowsViewModel::class.java)
        if(followers){
            followsViewModel.getAllFollowers(uid?:"").observe(this, Observer { followsView->
                followsView?.let {
                    adapter.setFollows(it)
                } })
        }else{
            followsViewModel.getAllFollows(uid?:"").observe(this, Observer { followsView->
                followsView?.let {
                    adapter.setFollows(it)
                } })
        }
    }
    private fun initRV() {
        adapter= FollowsAdapter(context!!,R.layout.row_follow)
        rvFollows.adapter=adapter
        rvFollows.layoutManager= LinearLayoutManager(context)
    }
}
