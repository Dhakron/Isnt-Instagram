package net.abrudan.isntinstagram.adapters

import android.content.Context
import android.net.Uri
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_post.view.*
import net.abrudan.isntinstagram.model.Post

class UserAdapter(val context:Context, val layout:Int): RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    private var dataList : List<Post?> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item!!)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setPost(postViews: List<Post?>) {
        this.dataList = postViews
        notifyDataSetChanged()
    }

    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Post){
            Picasso.get().load(Uri.parse(dataItem.media)).into(itemView.ivMedia)
            itemView.tvTittle.text=dataItem.tittle
            itemView.tvUserName.text=dataItem.owner
            itemView.tvComments.text=dataItem.comments.toString()
            itemView.tvDate.text=dataItem.date.toString()
            itemView.tvLikes.text=dataItem.likes.toString()
            itemView.tag=dataItem.uid
        }
    }
}