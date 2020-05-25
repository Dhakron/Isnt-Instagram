package net.abrudan.isntinstagram.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.core.ImagePipeline
import com.facebook.imagepipeline.producers.PostprocessorProducer
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.imagepipeline.request.Postprocessor
import kotlinx.android.synthetic.main.row_gallery.view.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.MediaPost

class GalleryAdapter(val context: Context, val layout:Int, val listener: GalleryAdapterInterface, val estimatedImageSize:Int):
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>(){
    init{
        setHasStableIds(true)
    }
    private var resizeOptions:ResizeOptions?=null
    private var dataList : List<MediaPost> = emptyList()
    override fun getItemId(position: Int): Long {
        // return items[position].id
        return dataList[position].hashCode().toLong()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]

        holder.bind(item!!,listener,resizeOptions,estimatedImageSize)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    fun getItems(): List<MediaPost> {
        return dataList
    }

    internal fun setGallery(newList: List<MediaPost>) {
        val oldList = this.dataList
        val diffResult:DiffUtil.DiffResult = DiffUtil.calculateDiff(
            GalleryItemDiffCallBack(oldList,newList)
        )

        this.dataList = newList
        diffResult.dispatchUpdatesTo(this)
    }
    class GalleryItemDiffCallBack(var oldList:List<MediaPost>, var newList:List<MediaPost>):DiffUtil.Callback(){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id== newList[newItemPosition].id
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }

    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        private var selecting=false
        fun bind(
            dataItem: MediaPost,
            listener: GalleryAdapterInterface,
            resizeOptions: ResizeOptions?,
            estimatedImageSize: Int
        ) {
            if(dataItem.selecting){
                itemView.tvSelected.visibility=TextView.VISIBLE
                itemView.tvSelected.setBackgroundResource(R.drawable.ic_unselected_image_count)
                if(dataItem.selectedPosition>0){
                    itemView.tvSelected.setBackgroundResource(R.drawable.ic_selected_image_count)
                    itemView.tvSelected.text=dataItem.selectedPosition.toString()
                }else{
                    itemView.tvSelected.text=""
                }
            }else{
                itemView.tvSelected.visibility=TextView.INVISIBLE
            }
            if(dataItem.selected){
                itemView.ivOverlay.visibility=ImageView.VISIBLE
            }else{
                itemView.ivOverlay.visibility=ImageView.INVISIBLE
            }
            var resizeOptions = resizeOptions
            val localResizeOptions = if (resizeOptions == null) {
                itemView.ivPhoto.doOnLayout {
                    resizeOptions = ResizeOptions(150, 150)
                }
                ResizeOptions(150, 150)
            } else resizeOptions
            if (dataItem.imgURI != null) {
                val request =
                ImageRequestBuilder.newBuilderWithSource(dataItem.imgURI).setShouldDecodePrefetches(true)
                    .setResizeOptions(localResizeOptions)
                    .build()
                var hierarchy =
                GenericDraweeHierarchyBuilder.newInstance(context.resources)
                    .setPlaceholderImage(R.color.colorPrimaryDark).build()
                itemView.ivPhoto.hierarchy=hierarchy
                itemView.ivPhoto.setImageRequest(request)
                itemView.ivPhoto.setOnLongClickListener {
                    listener.onLongClickGalleryItem(dataItem,itemView)
                    true
                }
                itemView.ivPhoto.setOnClickListener {
                    listener.onClickGalleryItem(dataItem,itemView)
                }
            }
        }
    }
    interface GalleryAdapterInterface {
        fun onClickGalleryItem(mediaPost: MediaPost,itemView:View) {

        }

        fun onLongClickGalleryItem(mediaPost: MediaPost,itemView:View) {

        }
    }
}