package net.abrudan.isntinstagram.views.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.fragment_comments.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.CommentsAdapter
import net.abrudan.isntinstagram.model.Comment
import net.abrudan.isntinstagram.viewModel.CommentsViewModel
import net.abrudan.isntinstagram.viewModel.GlobalViewModel
import java.util.*
import kotlin.collections.HashMap


/**
 * A simple [Fragment] subclass.
 */
class CommentsFragment : Fragment(),CommentsAdapter.CommentsAdapterInterface {

    private lateinit var adapter:CommentsAdapter
    private var replyTo:Comment?=null
    private var positionComment:Int?=null
    private val commentsViewModel: CommentsViewModel by lazy {
        CommentsViewModel(requireActivity().application)
    }
    private val globalViewModel: GlobalViewModel by lazy {
        ViewModelProvider(activity!!).get(GlobalViewModel::class.java)
    }
    private var postRef:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }
    override fun onStart() {
        super.onStart()
        postRef= arguments?.getString("postRef").toString()
        initRV()
        commentsViewModel.getAllComments().observe(this, Observer { commentView->
          commentView.let {
              adapter.setComment(it.sortedByDescending { it!!.date })
              srLayout.isRefreshing=false
            } })
        commentsViewModel.loadAllCommentsSync(postRef!!)
        srLayout.setOnRefreshListener {
            commentsViewModel.loadAllCommentsSync(postRef!!)
        }
        tvPost.setOnClickListener { addComment() }
        ivQuitReply.setOnClickListener{ quitReply() }
    }

    private fun initRV() {
        adapter= CommentsAdapter(context!!,R.layout.row_comment,this,0)
        rvComments.adapter=adapter
        rvComments.layoutManager= LinearLayoutManager(context)
        rvComments.addOnScrollListener(
            object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(recyclerView.layoutManager?.itemCount!!<9){
                        //return
                    }
                    if (!commentsViewModel.loadingData()&&dy>0&&recyclerView.layoutManager?.childCount!! +
                        (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        >= recyclerView.layoutManager?.itemCount!! - 2) {
                        commentsViewModel.loadAllCommentsFromLastSync(postRef!!)
                    }
                }
            }
        )
    }

    private fun addComment(){
        if(!etComment.text.toString().isNullOrEmpty()){
            etComment.isEnabled=false
            tvPost.isEnabled=false
            if(replyTo==null){
                commentsViewModel.addComment(etComment.text.toString(),postRef!!,null).addOnSuccessListener {
                    val data= it.data as HashMap<String,String>
                    val commentId=data["id"]
                    var myUser=globalViewModel.getMyUserData().value!!
                    val comment= Comment(myUser.userID,myUser.imageProfile,myUser.profileImgURI,myUser.uid,Date().time,
                        etComment.text.toString(),0,0,false,false,false,
                        mutableListOf(),postRef)
                    comment.commentRef="$postRef/Comments/$commentId"
                    var list=adapter.dataList.toMutableList()
                    list.add(comment)
                    adapter.setComment(list.sortedByDescending { it!!.date })
                    etComment.text.clear()
                    etComment.isEnabled=true
                    tvPost.isEnabled=true
                }.addOnFailureListener {
                    Toast.makeText(context, "Error",Toast.LENGTH_SHORT).show()
                    etComment.isEnabled=true
                    tvPost.isEnabled=true
                }
            }else{
                commentsViewModel.addComment(etComment.text.toString(),postRef!!,replyTo!!.commentRef).addOnSuccessListener {
                    val data= it.data as HashMap<String,String>
                    val replyId=data["id"]
                    var myUser=globalViewModel.getMyUserData().value!!
                    val reply=Comment(myUser.userID,myUser.imageProfile,myUser.profileImgURI,myUser.uid,Date().time,
                    etComment.text.toString(),0,0,false,false,true,
                        mutableListOf(), replyTo!!.postRef)
                    reply.commentRef=replyTo!!.commentRef
                    reply.replyRef=replyTo!!.commentRef+"/Replies/"+replyId
                    reply.isReply=true
                    var list=adapter.dataList.toMutableList()
                    val comment= list[positionComment!!]!!
                    comment.replies = comment.replies?.plus(1)
                    comment.repliesList?.add(reply)
                    comment.repliesList.sortBy { it!!.date }
                    comment.viewReplies=true
                    list[positionComment!!] = comment
                    adapter.setComment(list.sortedByDescending { it!!.date })
                    quitReply()
                    etComment.isEnabled=true
                    tvPost.isEnabled=true
                }.addOnFailureListener {
                    Toast.makeText(context, "Error",Toast.LENGTH_SHORT).show()
                    etComment.isEnabled=true
                    tvPost.isEnabled=true
                }
            }
        }
    }

    override fun reply(position: Int, comment:Comment) {
        rvComments.smoothScrollToPosition(position)
        tvReplyTo.text= getString(R.string.tv_reply_to)+" "+comment.owner
        cvReplyTo.visibility=View.VISIBLE
        etComment.setText("@${comment.owner} ")
        etComment.requestFocus(etComment.text.length)
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etComment, InputMethodManager.SHOW_IMPLICIT)
        replyTo=comment
        positionComment = position
        super.reply(position, comment)
    }

    override fun loadReplies(comment: Comment,position: Int) {
        if(comment.repliesList.size<=0){
            commentsViewModel.loadReplies(comment)
        }else{
            commentsViewModel.loadRepliesFrom(comment)
        }
        super.loadReplies(comment,position)
    }

    override fun likeComment(commentRef: String, liked: Boolean) {
        if(liked){
            commentsViewModel.unLikeComment(commentRef)
        }else{
            commentsViewModel.likeComment(commentRef)
        }
        super.likeComment(commentRef, liked)
    }
    private fun quitReply(){
        cvReplyTo.visibility=View.GONE
        etComment.text.clear()
        etComment.clearFocus()
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm!!.hideSoftInputFromWindow(etComment.windowToken, 0);
        replyTo=null
        positionComment=null
    }
}
