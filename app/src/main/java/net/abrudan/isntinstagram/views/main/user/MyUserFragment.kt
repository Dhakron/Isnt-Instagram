package net.abrudan.isntinstagram.views.main.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_my_user.*
import kotlinx.android.synthetic.main.fragment_my_user.llFollowers
import kotlinx.android.synthetic.main.fragment_my_user.llFollowing
import kotlinx.android.synthetic.main.fragment_my_user.tvNumFollowers
import kotlinx.android.synthetic.main.fragment_my_user.tvNumFollowing
import kotlinx.android.synthetic.main.fragment_my_user.tvNumPosts
import kotlinx.android.synthetic.main.fragment_my_user.tvUserName
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.HomeAdapter
import net.abrudan.isntinstagram.model.Post
import net.abrudan.isntinstagram.util.SaveViewPagerPosition
import net.abrudan.isntinstagram.viewModel.GlobalViewModel
import net.abrudan.isntinstagram.viewModel.UserViewModel
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton


class MyUserFragment : Fragment(),HomeAdapter.HomeAdapterInterface {

    private val userViewModel: UserViewModel by lazy {
        UserViewModel()
    }
    private lateinit var globalViewModel:GlobalViewModel
    private lateinit var  adapter: HomeAdapter
    private lateinit var uid:String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_user, container, false)
    }
    override fun onStart() {
        super.onStart()
        initRV()
        uid=arguments?.getString("uid")?:userViewModel.getUID()
        globalViewModel= ViewModelProvider(activity!!).get(GlobalViewModel::class.java)
        globalViewModel.loadMyuserData()
        userViewModel.loadAllPosts(uid)
        userViewModel.getUserData(uid).observe(this, Observer { user->
            user?.let {
                Picasso.get().load(it.profileImgURI).placeholder(R.drawable.ic_userdefault).into(ivThumb)
                tvNumFollowers.text=it.numFollowers.toString()
                tvNumFollowing.text=it.numFollows.toString()
                tvNumPosts.text=it.numPosts.toString()
                tvUserName.text=it.name.toString()
                tvUserId.text=it.userID.toString()
                tvUserBio.text=it.bio
            }
        })
        userViewModel.getAllPosts().observe(this, Observer { postView->
            postView?.let {
                Log.e("myusdiiid",it.toString())
                adapter.setPost(it.sortedBy { it!!.date }.reversed())
                srLayout.isRefreshing=false
                progressBar.visibility=View.GONE
            } })
        srLayout.setOnRefreshListener {
            userViewModel.loadAllPosts(uid)
        }
        llFollowers.setOnClickListener {
            findNavController().navigate(MyUserFragmentDirections.actionNavigationUserToFollowsFragment(uid,true))
        }
        llFollowing.setOnClickListener {
            findNavController().navigate(MyUserFragmentDirections.actionNavigationUserToFollowsFragment(uid,false))
        }
        ivLogOut.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            findNavController().popBackStack()
            findNavController().navigate(MyUserFragmentDirections.actionGlobalStartFragment())
        }
        btnEditProfile.setOnClickListener {
            findNavController().navigate(MyUserFragmentDirections.actionNavigationUserToEditUserFragment())
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
                        userViewModel.loadAllPostsFromLast(uid)
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
    override fun onclickDelete(data: Post) {
        alert(getString(R.string.alert_delete_post)) {
            title =getString(R.string.alert)
            yesButton {
                userViewModel.deletePost(data)
                var tempList=adapter.getPosts().toMutableList()
                tempList.remove(data)
                adapter.setPost(tempList.sortedBy { it!!.date }.reversed())
            }
            noButton { }
        }.show()
        super.onclickDelete(data)
    }
}