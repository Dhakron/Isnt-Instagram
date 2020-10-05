package net.abrudan.isntinstagram.views.main.user

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.progressBar

import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.HomeAdapter
import net.abrudan.isntinstagram.model.Post
import net.abrudan.isntinstagram.util.SaveViewPagerPosition
import net.abrudan.isntinstagram.viewModel.UserViewModel
import net.abrudan.isntinstagram.views.main.home.HomeFragmentDirections

class UserFragment : Fragment(),HomeAdapter.HomeAdapterInterface {
    private val userViewModel: UserViewModel by lazy {
        UserViewModel()
    }
    private lateinit var  adapter: HomeAdapter
    private lateinit var uid:String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }
    override fun onStart() {
        super.onStart()
        initRV()
        uid=arguments?.getString("uid")?:userViewModel.getUID()
        userViewModel.loadFollow(uid)
        userViewModel.getFollow().observe(this, Observer {
            if(it){
                btnFollow.setTextColor(Color.BLACK)
                btnFollow.text=getString(R.string.btn_following)
                btnFollow.setBackgroundResource(R.drawable.ic_btn_selected)
            }else{
                btnFollow.setTextColor(Color.BLACK)
                btnFollow.text=getString(R.string.btn_follow)
                btnFollow.setBackgroundResource(R.drawable.ic_defaultbtn)
            }
        })
        userViewModel.loadAllPostsSync(uid)
        userViewModel.getUserData(uid).observe(this, Observer { user->
            user?.let {
                Picasso.get().load(it.profileImgURI).placeholder(R.drawable.ic_userdefault).into(ivThumb)
                tvNumFollowers.text=it.numFollowers.toString()
                tvNumFollowing.text=it.numFollows.toString()
                tvNumPosts.text=it.numPosts.toString()
                tvUserName.text=it.name.toString()
                tvUserId.text=it.userID.toString()
                tvUserId.text=it.userID.toString()
            }
        })
        userViewModel.getAllPosts().observe(this, Observer { postView->
            postView?.let {
                adapter.setPost(it.sortedBy { it!!.date }.reversed())
                srLayout.isRefreshing=false
                progressBar.visibility=View.GONE
            } })
        srLayout.setOnRefreshListener {
            userViewModel.loadAllPostsSync(uid)
        }
        llFollowers.setOnClickListener {
            findNavController().navigate(UserFragmentDirections.actionUserFragmentToFollowsFragment(uid,true))
        }
        llFollowing.setOnClickListener {
            findNavController().navigate(UserFragmentDirections.actionUserFragmentToFollowsFragment(uid,false))
        }
        ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        btnFollow.setOnClickListener {
            if(userViewModel.getFollow().value==true){
                userViewModel.unFollow(uid)
                userViewModel.followUser.value=false
            }else{
                userViewModel.follow(uid)
                userViewModel.followUser.value=true
            }
        }
    }

    private fun initRV() {
        adapter= HomeAdapter(context!!,R.layout.row_post, SaveViewPagerPosition(),this)
        rvUser.adapter=adapter
        rvUser.layoutManager= LinearLayoutManager(context)
        rvUser.addOnScrollListener(
            object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(recyclerView.layoutManager?.itemCount!!<9){
                        //return
                    }
                    if (!userViewModel.loadingData()&&dy>0&&recyclerView.layoutManager?.childCount!! +
                        (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        >= recyclerView.layoutManager?.itemCount!! - 1) {
                        userViewModel.loadAllPostsFromLastSync(uid)
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
        userViewModel.likePost(data)
    }

    override fun viewComments(postRef:String) {
        findNavController().navigate(HomeFragmentDirections.actionGlobalCommentsFragment(postRef))
        super.viewComments(postRef)
    }

}