package net.abrudan.isntinstagram.adapters

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_row_media_item_view.view.*
import org.jetbrains.anko.displayMetrics


class PostMediaAdapter(listuri:List<Uri>): PagerAdapter() {
    private var listUri = listuri

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = LayoutInflater.from(container.context)
            .inflate(net.abrudan.isntinstagram.R.layout.fragment_row_media_item_view, container, false)
        val imageViewCampaign: ImageView =
            itemView.ivMedia2
        val uri:Uri = listUri[position]
        var width=container.context.displayMetrics.widthPixels
        var height=(width/4)*3
        imageViewCampaign.layoutParams=LinearLayout.LayoutParams(width,height)
        Picasso.get().load(uri).into(imageViewCampaign)
        container.addView(itemView)
        return itemView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView((`object` as View))
    }

    override fun getCount(): Int {
        return listUri.size
    }
}