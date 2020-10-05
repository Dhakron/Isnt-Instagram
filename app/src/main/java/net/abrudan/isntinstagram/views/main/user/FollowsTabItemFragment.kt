package net.abrudan.isntinstagram.views.main.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_follows_tab_item.*
import kotlinx.android.synthetic.main.fragment_follows_tab_item.progressBar
import kotlinx.android.synthetic.main.fragment_home.*

import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.FollowsAdapter
import net.abrudan.isntinstagram.model.UserInfo
import net.abrudan.isntinstagram.viewModel.FollowsViewModel

class FollowsTabItemFragment : Fragment(),FollowsAdapter.FollowsAdapterInterface {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follows_tab_item, container, false)
    }

    private lateinit var followsViewModel: FollowsViewModel
    private lateinit var adapter: FollowsAdapter
    private var uid:String?=null
    private var followers:Boolean?=null
    override fun onStart() {
        super.onStart()
        initRV()
        followers= arguments?.getBoolean("followers")
        uid = arguments?.getString("uid")
        followsViewModel = ViewModelProvider(this).get(FollowsViewModel::class.java)
        if(followers!!){
            followsViewModel.getAllFollowers().removeObservers(viewLifecycleOwner)
            followsViewModel.getAllFollowers().observe(viewLifecycleOwner, Observer { followsView->
                followsView?.let {
                    adapter.setFollows(it.sortedBy { it!!.userID })
                    progressBar.visibility=View.GONE
                } })
            followsViewModel.loadAllFollowersSync(uid!!)
        }else{
            followsViewModel.getAllFollowing().removeObservers(viewLifecycleOwner)
            followsViewModel.getAllFollowing().observe(viewLifecycleOwner, Observer { followsView->
                followsView?.let {
                    adapter.setFollows(it.sortedBy { it!!.userID })
                    progressBar.visibility=View.GONE
                } })
            followsViewModel.loadAllFollowingSync(uid!!)
        }
    }

    override fun onPause() {
        super.onPause()
        if(followers!!){
            followsViewModel.getAllFollowers().removeObservers(this)
        }else{
            followsViewModel.getAllFollowing().removeObservers(this)
        }
    }
    private fun initRV() {
        adapter= FollowsAdapter(context!!,R.layout.row_follow,this)
        rvFollow.adapter=adapter
        rvFollow.layoutManager= LinearLayoutManager(context)
        rvFollow.addOnScrollListener(
            object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(recyclerView.layoutManager?.itemCount!!<9){
                        //return
                    }
                    if (!followsViewModel.loadingData()&&dy>0&&recyclerView.layoutManager?.childCount!! +
                        (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        >= recyclerView.layoutManager?.itemCount!! - 1) {
                        if(followers!!){
                            followsViewModel.loadAllFollowersFromSync(uid?:"")
                        }else{
                            followsViewModel.loadAllFollowingFromSync(uid?:"")
                        }
                        progressBar.visibility=View.VISIBLE
                    }
                }
            }
        )
    }

    override fun onClick(uid: String) {
        findNavController().navigate(FollowsTabItemFragmentDirections.actionGlobalUserFragment(uid))
        super.onClick(uid)
    }

    override fun btnFollowClick(data: UserInfo) {
        if(data.followYou==true){
            followsViewModel.unFollow(data.userUID!!)
            var templist= adapter.getFollows().toMutableList()
            templist.find { it!!.userUID==data.userUID }!!.followYou=false
            adapter.setFollows(templist.sortedBy { it!!.userID })
        }else{
            followsViewModel.follow(data.userUID!!)
            var templist= adapter.getFollows().toMutableList()
            templist.find { it!!.userUID==data.userUID }!!.followYou=true
            adapter.setFollows(templist.sortedBy { it!!.userID })
        }

        super.btnFollowClick(data)
    }

}