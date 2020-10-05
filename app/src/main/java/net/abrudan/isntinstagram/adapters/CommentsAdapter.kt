package net.abrudan.isntinstagram.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import kotlinx.android.synthetic.main.row_comment.view.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.Comment
import net.abrudan.isntinstagram.util.getTimeDiffForComments

class CommentsAdapter(val context: Context, val layout:Int, val listener: CommentsAdapterInterface, val positionFather: Int): RecyclerView.Adapter<CommentsAdapter.ViewHolder>(){

    var dataList : List<Comment?> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item!!,listener,position,this,positionFather)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setComment(postViews: List<Comment?>) {
        this.dataList = postViews
        notifyDataSetChanged()
    }
    fun getComments():List<Comment?> {
        return this.dataList
    }

    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Comment,listener: CommentsAdapterInterface,position: Int,adapter: CommentsAdapter, positionFather:Int) {
            itemView.ivThumb.load(dataItem.profileImgURI)
            itemView.tvComment.text=dataItem.owner+" "+dataItem.comment
            itemView.tvDate.text=dataItem.date?.getTimeDiffForComments(context)
            if(dataItem.likes!!>0){
                itemView.tvLikes.text=dataItem.likes.toString()+" "+context.getString(R.string.tv_like)
            }else{
                itemView.tvLikes.text=""
            }
            if (dataItem.liked!!) itemView.ivLike.setBackgroundResource(R.drawable.ic_like)
            else itemView.ivLike.setBackgroundResource(R.drawable.ic_unlike)
            itemView.repliesContainer.visibility=View.GONE
            if(dataItem.replies!!>0){
                if(dataItem.viewReplies&&!dataItem.isReply){
                    itemView.repliesContainer.visibility=View.VISIBLE
                    var adapterReply= CommentsAdapter(context!!,R.layout.row_comment,listener,position)
                    adapterReply.setComment(dataItem.repliesList!!.toList())
                    itemView.rvReplies.adapter=adapterReply
                    itemView.rvReplies.layoutManager= LinearLayoutManager(context)
                }else{
                    itemView.repliesContainer.visibility=View.GONE
                }
                itemView.tvReplies.visibility=View.VISIBLE
                if(dataItem.loadingReply){
                    itemView.tvReplies.text=context.getText(R.string.tv_loading_replies)
                }else{
                    if(dataItem.replies!=dataItem.repliesList.size){
                        itemView.tvReplies.text=loadRepliesText(dataItem.repliesList,dataItem.replies!!,context)
                        itemView.tvReplies.setOnClickListener{
                            listener.loadReplies(dataItem,position)
                            adapter.dataList[position]!!.loadingReply=true
                            adapter.notifyItemChanged(position)
                        }
                    }else{
                        itemView.tvReplies.visibility=View.GONE
                    }
                }
            }else{
                itemView.tvReplies.visibility=View.GONE
            }
            if(dataItem.isReply){
                itemView.ivLike.setOnClickListener{
                    listener.likeComment(dataItem.replyRef!!,dataItem.liked!!)
                    adapter.dataList[position]!!.liked=!dataItem.liked!!
                    adapter.dataList[position]!!.likes=dataItem.likes!!+if(dataItem.liked!!)1 else -1
                    adapter.notifyItemChanged(position)
                }
                itemView.tvReplies.visibility=View.GONE
                itemView.tvReply.setOnClickListener {
                    listener.reply(positionFather,dataItem)
                }
            }else{
                itemView.ivLike.setOnClickListener{
                    listener.likeComment(dataItem.commentRef!!,dataItem.liked!!)
                    adapter.dataList[position]!!.liked=!dataItem.liked!!
                    adapter.dataList[position]!!.likes=dataItem.likes!!+if(dataItem.liked!!)1 else -1
                    adapter.notifyItemChanged(position)
                }
                itemView.tvReply.setOnClickListener {
                    listener.reply(position,dataItem)
                }
            }
        }
        fun loadRepliesText(
            repliesLoaded: MutableList<Comment?>,
            repliesCount:Int,
            context: Context): String {
            if(repliesLoaded.isEmpty()){
                return context.getText(R.string.tv_front_show_replies).toString()+" "+repliesCount+" "+context.getText(R.string.tv_end_show_replies).toString()
            }else{
                var countRepliesToLoad= repliesCount-repliesLoaded.size
                return context.getText(R.string.tv_front_show_previous_replies).toString()+" "+countRepliesToLoad+" "+context.getText(R.string.tv_end_show_previous_replies).toString()
            }
        }
    }

    interface CommentsAdapterInterface {
        fun likeComment(commentRef:String,liked:Boolean) {

        }

        fun loadReplies(comment: Comment,position: Int){

        }
        fun reply(position: Int, comment:Comment){

        }
    }


}