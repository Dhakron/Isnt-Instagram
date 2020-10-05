package net.abrudan.isntinstagram.adapters

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.api.load
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.row_post.view.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.Post
import net.abrudan.isntinstagram.util.SaveViewPagerPosition
import net.abrudan.isntinstagram.util.getTimeDiff
import org.jetbrains.anko.displayMetrics
import java.util.*

class HomeAdapter(val context:Context,val layout:Int,saveViewPagerPosition: SaveViewPagerPosition,val listener: HomeAdapterInterface): RecyclerView.Adapter<HomeAdapter.ViewHolder>(){

    private var saveViewPager=saveViewPagerPosition
    private var dataList : List<Post?> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context,saveViewPager)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item!!,listener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setPost(postViews: List<Post?>) {
        this.dataList = postViews
        notifyDataSetChanged()
    }
     fun getPosts():List<Post?> {
        return this.dataList
    }
    class ViewHolder(viewlayout: View, val context: Context, val saveViewPagerPosition: SaveViewPagerPosition) : RecyclerView.ViewHolder(viewlayout) {
        private val currentUID=FirebaseAuth.getInstance().currentUser!!.uid
        fun bind(dataItem: Post,listener: HomeAdapterInterface) {
            itemView.ivLikeAnimation.visibility=View.GONE
            var width = context.displayMetrics.widthPixels
            var height = (width / 4) * 3
            itemView.vpMedia.layoutParams = LinearLayout.LayoutParams(width, height)
            if (!saveViewPagerPosition.containItem(dataItem.id)) {
                saveViewPagerPosition.addNewItem(dataItem.id, dataItem.mediaUri!!.size)
            } else {
                saveViewPagerPosition.setMaxPosition(dataItem.id, dataItem.mediaUri!!.size)
                itemView.vpMedia.adapter?.notifyDataSetChanged()
            }

            if (dataItem.adapter == null) {
                dataItem.adapter = AdapterPostImageItem(
                    context!!,
                    R.layout.fragment_row_media_item_view,
                    saveViewPagerPosition,
                    dataItem,listener,itemView
                )
            }
            dataItem.adapter!!.setItems(dataItem.mediaUri!!.toList())
            //Picasso.get().load(dataItem.thumbUri).placeholder(R.drawable.ic_userdefault).into(itemView.ivThumb)
            itemView.ivThumb.load(dataItem.thumbUri)
            itemView.vpMedia.adapter = dataItem.adapter
            if (dataItem.media!!.size > 1) {
                TabLayoutMediator(itemView.tab_layout, itemView.vpMedia)
                { tab, position -> }.attach()
                itemView.tvImgCount.visibility = View.VISIBLE
                itemView.tvImgCount.text = "1/" + dataItem.media!!.size
                itemView.tvImgCount.setBackgroundResource(R.drawable.rounded_corner)
                itemView.vpMedia.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        itemView.tvImgCount.text =
                            (position + 1).toString() + "/" + dataItem.media!!.size
                        super.onPageSelected(position)
                    }
                })
            } else {
                itemView.tvImgCount.visibility = View.GONE
                itemView.tab_layout.visibility = View.GONE
            }
            if(dataItem.liked==true){
                itemView.ivLike.setBackgroundResource(R.drawable.ic_like)
            }else{
                itemView.ivLike.setBackgroundResource(R.drawable.ic_unlike)
            }
            itemView.vpMedia.setCurrentItem(
                saveViewPagerPosition.getLastPosition(dataItem.id),
                false
            )
            itemView.tvTittle.text = dataItem.owner + ": " + dataItem.tittle
            itemView.tvUserName.text = dataItem.owner
            itemView.tvComments.text =
                dataItem.comments.toString() + " " + context.getString(R.string.tv_comments)
            itemView.tvDate.text = dataItem.date?.getTimeDiff(context)
            itemView.tvLikes.text = dataItem.likes.toString() + " " + context.getString(R.string.tv_likes)
            if(dataItem.uid==currentUID){
                itemView.ivMenu.setBackgroundResource(R.drawable.ic_basura)
                itemView.ivMenu.setOnClickListener {
                    Log.e("sksdhfajskdfha","asdkhfakñsdhflkajsdhfjkasdfasd")
                    listener.onclickDelete(dataItem)
                }
            }
            itemView.ivLike.setOnClickListener{
                listener.likePost(itemView,dataItem)
                }
            if(dataItem.likeClick==false){
                itemView.ivLikeAnimation.visibility=View.VISIBLE
                val hearth=itemView.ivLikeAnimation
                hearth.alpha=0.7F
                val drawable = hearth.drawable
                var avd=drawable as AnimatedVectorDrawable
                avd.start()
            }else{
                itemView.ivLikeAnimation.visibility=View.GONE
            }
            itemView.ivThumb.setOnClickListener{
                listener.onClick(dataItem.uid!!)
            }
            itemView.tvUserName.setOnClickListener{
                listener.onClick(dataItem.uid!!)
            }
            itemView.ivComment.setOnClickListener {
                var postRef="UsersData/"+dataItem.uid+"/Posts/"+dataItem.originalId
                listener.viewComments(postRef)
            }
        }
    }
    interface HomeAdapterInterface {
        fun likePost(view:View,data:Post) {
        }
        fun onClick(uid:String){
        }
        fun viewComments(postRef:String){

        }
        fun onclickDelete(data: Post){

        }
    }
}