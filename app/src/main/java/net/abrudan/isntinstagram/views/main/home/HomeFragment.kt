package net.abrudan.isntinstagram.views.main.home


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.HomeAdapter
import net.abrudan.isntinstagram.model.Post
import net.abrudan.isntinstagram.util.SaveViewPagerPosition
import net.abrudan.isntinstagram.viewModel.GlobalViewModel
import net.abrudan.isntinstagram.viewModel.HomeViewModel
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread


class HomeFragment : Fragment(),HomeAdapter.HomeAdapterInterface {
    private val globalViewModel: GlobalViewModel by lazy {
        ViewModelProvider(activity!!).get(GlobalViewModel::class.java)
    }

    private val homeViewModel: HomeViewModel by lazy {
        HomeViewModel(requireActivity().application)
    }
    private lateinit var  adapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()
        initRV()
        homeViewModel.getAllPosts().observe(this, Observer { postView->
            postView?.let {
                adapter.setPost(it.sortedBy { it!!.date }.reversed())
                srLayout.isRefreshing=false
                progressBar.visibility=View.GONE
            } })
        homeViewModel.loadAllPostsSync()
        srLayout.setOnRefreshListener {
            homeViewModel.loadAllPostsSync()
        }
        ivLogo.setOnClickListener {
            rvHome.smoothScrollToPosition(0)
        }
    }

    private fun initRV() {
        adapter= HomeAdapter(context!!,R.layout.row_post, SaveViewPagerPosition(),this)
        rvHome.adapter=adapter
        rvHome.layoutManager= LinearLayoutManager(context)
        rvHome.addOnScrollListener(
            object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(recyclerView.layoutManager?.itemCount!!<9){
                        //return
                    }
                    if (!homeViewModel.loadingData()&&dy>0&&recyclerView.layoutManager?.childCount!! +
                        (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        >= recyclerView.layoutManager?.itemCount!! - 2) {
                        homeViewModel.loadAllPostsFromLastSync()
                        progressBar.visibility=View.VISIBLE
                    }
                }
            }
        )
    }

    override fun likePost(view: View,data: Post) {
        super.likePost(view,data)
        var tempList=adapter.getPosts().toMutableList()
        tempList.remove(data)
        var tempData= data
        tempData.likeClick=false
        tempList.add(tempData)
        adapter.setPost(tempList.sortedBy { it!!.date }.reversed())
        homeViewModel.likePost(data)
    }

    override fun onClick(uid: String) {
        findNavController().navigate(HomeFragmentDirections.actionGlobalUserFragment(uid))
        super.onClick(uid)
    }

    override fun viewComments(postRef:String) {
        findNavController().navigate(HomeFragmentDirections.actionGlobalCommentsFragment(postRef))
        super.viewComments(postRef)
    }
}