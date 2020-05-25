package net.abrudan.isntinstagram.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_search.view.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.UserInfo

class SearchViewAdapter(val context:Context, val layout:Int, val listener:SearchViewAdapterInterface): RecyclerView.Adapter<SearchViewAdapter.ViewHolder>(){

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

    internal fun setUserInfo(postViews: List<UserInfo?>) {
        this.dataList = postViews.sortedByDescending { it?.name }
        notifyDataSetChanged()
    }

    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: UserInfo,listener:SearchViewAdapterInterface){
            Picasso.get().load(dataItem.profileImgURI).placeholder(R.drawable.ic_userdefault).into(itemView.ivThumb)
            itemView.tvUserID.text=dataItem.userID
            itemView.tvName.text=dataItem.name
            itemView.tag=dataItem.userUID
            itemView.setOnClickListener{
                listener.click(dataItem)
            }
        }
    }
    interface SearchViewAdapterInterface{
        fun click(user:UserInfo){

        }
    }
}