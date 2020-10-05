package net.abrudan.isntinstagram.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_follow.view.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.UserInfo


class FollowsAdapter(val context:Context, val layout:Int, val listener:FollowsAdapterInterface): RecyclerView.Adapter<FollowsAdapter.ViewHolder>(){

    private var dataList : List<UserInfo?> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item!!,listener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setFollows(followViews: List<UserInfo?>) {
        this.dataList = followViews
        notifyDataSetChanged()
    }
    fun getFollows():List<UserInfo?>{
        return dataList
    }

    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        private val currentUser=FirebaseAuth.getInstance().currentUser
        fun bind(dataItem: UserInfo,listener:FollowsAdapterInterface){
            //Picasso.get().load(dataItem.profileImgURI).placeholder(R.drawable.ic_userdefault).into(itemView.ivThumb)
            itemView.ivThumb.load(dataItem.profileImgURI)
            itemView.tvUserID.text=dataItem.userID
            itemView.tvName.text=dataItem.name
            if(currentUser!!.uid != dataItem.userUID){
                if(dataItem.followYou==true){
                    itemView.btnFollow.setBackgroundResource(R.drawable.ic_btn_selected)
                    itemView.btnFollow.setTextColor(Color.BLACK)
                    itemView.btnFollow.text=context.getString(R.string.btn_following)
                }else{
                    itemView.btnFollow.setBackgroundResource(R.drawable.ic_defaultbtn)
                    itemView.btnFollow.setTextColor(Color.WHITE)
                    itemView.btnFollow.text=context.getString(R.string.btn_follow)
                }
                itemView.ivThumb.setOnClickListener{
                    listener.onClick(dataItem.userUID!!)
                }
                itemView.tvUserID.setOnClickListener{
                    listener.onClick(dataItem.userUID!!)
                }
                itemView.btnFollow.setOnClickListener {
                    listener.btnFollowClick(dataItem)
                }
            }else{
                itemView.btnFollow.visibility=View.GONE
            }
        }
    }
    interface FollowsAdapterInterface{
        fun onClick(uid:String){
        }
        fun btnFollowClick(data:UserInfo){
        }
    }
}