package net.abrudan.isntinstagram.views.main.user

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_my_user.*

import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.UserAdapter
import net.abrudan.isntinstagram.viewModel.UserViewModel

class UserFragment : Fragment() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var  adapter: UserAdapter
    private lateinit var uid:String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user, container, false)
        return root
    }
    override fun onStart() {
        super.onStart()
        initRV()
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.getUserData().observe(this, Observer { user->
            user?.let {
                tvNumFollowers.text=it.numFollowers.toString()
                tvNumFollowing.text=it.numFollows.toString()
                tvNumPosts.text=it.numPosts.toString()
                tvUserName.text=it.userID.toString()
            }
        })
        userViewModel.getAllPosts().observe(this, Observer { postView->
            postView?.let {
                adapter.setPost(it)
            } })
        uid=arguments?.getString("uid")?:userViewModel.getUID()
        llFollowers.setOnClickListener {
            findNavController().navigate(UserFragmentDirections.actionUserFragmentToFollowsFragment2(true,""))
        }
        llFollowing.setOnClickListener {
            findNavController().navigate(UserFragmentDirections.actionUserFragmentToFollowsFragment2(false,""))
        }
    }
    private fun initRV() {
        adapter= UserAdapter(context!!,R.layout.row_post)
        rvUserPosts.adapter=adapter
        rvUserPosts.layoutManager= LinearLayoutManager(context)
    }
}
