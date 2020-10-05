package net.abrudan.isntinstagram.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_row_media_item_view.view.*
import net.abrudan.isntinstagram.model.Post
import net.abrudan.isntinstagram.util.SaveViewPagerPosition
import org.jetbrains.anko.displayMetrics

class AdapterPostImageItem(val context: Context, val layout:Int, saveViewPagerPosition: SaveViewPagerPosition, val parentDataItem:Post, val listener: HomeAdapter.HomeAdapterInterface,val view:View):
    RecyclerView.Adapter<AdapterPostImageItem.ViewHolder>(){
    init{
        setHasStableIds(true)
    }
    private var adapterId=parentDataItem.id
    private var saveViewPagerPosition=saveViewPagerPosition
    private var dataList : List<Uri> = emptyList()
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
        holder.bind(item!!,listener,parentDataItem,view)
    }
    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        saveViewPagerPosition.setLastPosition(adapterId,holder.layoutPosition)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    fun getItems(): List<Uri> {
        return dataList
    }

    internal fun setItems(newList: List<Uri>) {
        val oldList = this.dataList
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            GalleryItemDiffCallBack(oldList,newList)
        )
        this.dataList = newList
        diffResult.dispatchUpdatesTo(this)
    }
    class GalleryItemDiffCallBack(var oldList:List<Uri>, var newList:List<Uri>):DiffUtil.Callback(){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition]== newList[newItemPosition]
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

    class ViewHolder(
        viewlayout: View, val context: Context
    ) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Uri,listener: HomeAdapter.HomeAdapterInterface,parentDataitem: Post,parentView:View) {
            var doubleClickLastTime=0L
            itemView.ivMedia2.setOnClickListener{
                run {
                    if (System.currentTimeMillis() - doubleClickLastTime < 300) {
                        doubleClickLastTime = 0
                        listener.likePost(parentView,parentDataitem)
                    } else {
                        doubleClickLastTime = System.currentTimeMillis()
                    }
                }
            }
            var width=context.displayMetrics.widthPixels
            var height=(width/4)*3
            itemView.ivMedia2.layoutParams= LinearLayout.LayoutParams(width,height)
            //Picasso.get().load(dataItem).into(itemView.ivMedia2)
            itemView.ivMedia2.load(dataItem)
        }
    }
}
