package net.abrudan.isntinstagram.adapters

import android.content.Context
import android.net.Uri
import android.os.Build.ID
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_follow.view.*
import kotlinx.android.synthetic.main.row_post.view.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.UserInfo


class FollowsAdapter(val context:Context, val layout:Int): RecyclerView.Adapter<FollowsAdapter.ViewHolder>(){

    private var dataList : List<UserInfo?> = emptyList()

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

    internal fun setFollows(followViews: List<UserInfo?>) {
        this.dataList = followViews
        notifyDataSetChanged()
    }

    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: UserInfo){
            Picasso.get().load(dataItem.profileImgURI).into(itemView.ivProfileImg)
            itemView.tvUserID.text=dataItem.userID
            itemView.tvName.text=dataItem.name
            itemView.btnFollow.setOnClickListener{

            }
            itemView.tag=dataItem.userRef
        }

    }


}