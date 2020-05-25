package net.abrudan.isntinstagram.views.main.addPost

import android.app.DownloadManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.alpha
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.datasource.DataSubscriber
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.core.ImagePipeline
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.oginotihiro.cropview.CropUtil
import kotlinx.android.synthetic.main.fragment_add_post.*
import kotlinx.android.synthetic.main.fragment_crop_image.*
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery.cropView
import kotlinx.android.synthetic.main.row_gallery.view.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.GalleryAdapter
import net.abrudan.isntinstagram.model.MediaPost
import net.abrudan.isntinstagram.util.GalleryRowDecoration
import net.abrudan.isntinstagram.viewModel.GalleryViewModel
import org.jetbrains.anko.doAsync

class GalleryFragment : Fragment(), AdapterView.OnItemSelectedListener,GalleryAdapter.GalleryAdapterInterface {
    private lateinit var galleryViewModel:GalleryViewModel
    private var albumList= mutableListOf<String?>()
    private lateinit var adapter:GalleryAdapter
    private var currentAlbum="Gallery"
    private var loadedSpinner=false
    private var selectedList:MutableList<MediaPost> = mutableListOf()
    private var currentItemSelected:MediaPost? =null
    private var isClicking=false
    private var selecting=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onStart() {
        super.onStart()
        initRV()
        initImgLib()
        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        galleryViewModel.loadAllMedia(context!!).observe(this, Observer {
            adapter.setGallery(it)
            if(currentItemSelected==null){
                currentItemSelected=it[0]
                selectedList.clear()
                selectedList.add(currentItemSelected!!)
                it.map {
                    if(it==currentItemSelected) {
                        it.selected = true
                        return@map
                    }
                }
                adapter.setGallery(it)
                adapter.notifyDataSetChanged()
            }
            loadImgPreview()
        })
        galleryViewModel.loadAllAlbums().observe(this, Observer {
            albumList= mutableListOf()
            albumList.add("Gallery")
            albumList.add("Imagenes")
            //albumList.add("Video")
            albumList.addAll(it.toMutableList())
            loadAlbumSpinner()
        })
        btnNext.setOnClickListener{
            if(selectedList.size>0){
                    selectedList.last().bitMap=cropView.output
                    findNavController().navigate(GalleryFragmentDirections.actionGlobalUploadPostFragment(selectedList.toTypedArray()))
            }else{
                Toast.makeText(context,"Seleciona una foto",Toast.LENGTH_SHORT).show()
            }
        }
        btnSelection.setOnClickListener {
            selecting=!selecting
            if(selecting){
                btnSelection.setBackgroundResource(R.drawable.ic_selected_multiple_img_btn)
                btnSelection.setTextColor(Color.BLACK)
            }else{
                btnSelection.setBackgroundResource(R.drawable.ic_unselected_multiple_img_btn)
                btnSelection.setTextColor(Color.WHITE)
            }
            var list=adapter.getItems()
            list.map {
                it.selecting=selecting
                it.selectedPosition=0
            }
            selectedList.clear()
            adapter.setGallery(list)
            adapter.notifyDataSetChanged()
            onClickGalleryItem(currentItemSelected!!,View(context))
        }
    }
    fun loadImgPreview(){
        if(cropView!=null&&currentItemSelected!=null){
            cropView.of(currentItemSelected!!.imgURI).withAspect(4,3)
                .initialize(context)
        }
    }
    fun initImgLib(){
        val config = ImagePipelineConfig.newBuilder(context)
            .setDownsampleEnabled(true).setDiskCacheEnabled(true)
            .build()
        Fresco.initialize(context, config)
    }
    private fun initRV() {
        val estimatedImageSize = Resources.getSystem().displayMetrics.widthPixels / 4
        adapter= GalleryAdapter(context!!,R.layout.row_gallery,this,estimatedImageSize)
        rvGallery.adapter=adapter
        rvGallery.layoutManager = GridLayoutManager(context,4)
        rvGallery.setItemViewCacheSize(20)
        rvGallery.setHasFixedSize(true)
        rvGallery.addOnScrollListener(
            object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(recyclerView.layoutManager?.itemCount!!<100){
                        return
                    }
                    if (!galleryViewModel.loadingData()&&dy>0&&recyclerView.layoutManager?.childCount!! +
                        (recyclerView.layoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()
                        >= recyclerView.layoutManager?.itemCount!! - 20) {
                        galleryViewModel.loadMediaByAlbum(currentAlbum,selecting)
                    }
                }
            }
        )
    }

    private fun loadAlbumSpinner(){
        val adapter = ArrayAdapter(context!!,android.R.layout.simple_gallery_item,albumList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        albumSpinner.onItemSelectedListener = this
        albumSpinner.adapter = adapter
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onResume() {
        super.onResume()
        if(selectedList.size>0) {
            var list = adapter.getItems()
            val tempIds = ArrayList<Long>()
            selectedList.map { tempIds.add(it.id!!) }
            list.map {
                it.selecting = list[0].selecting
                if (tempIds.contains(it.id)) {
                    it.selectedPosition = tempIds.lastIndexOf(it.id)
                }
            }
            adapter.setGallery(list)
            adapter.notifyDataSetChanged()
        }
        loadImgPreview()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(loadedSpinner){
            galleryViewModel.restarLastMediaLoaded()
            currentAlbum = when(position) {
                0 -> "Gallery"
                1 -> "Images"
                else -> albumList[position]!!
            }
            galleryViewModel.loadMediaByAlbum(currentAlbum,selecting)
        }else{
            loadedSpinner=true
        }
    }

    override fun onLongClickGalleryItem(mediaPost: MediaPost, view: View) {
        if(!mediaPost.selecting) {
            btnSelection.setBackgroundResource(R.drawable.ic_selected_multiple_img_btn)
            btnSelection.setTextColor(Color.BLACK)
            selecting=true
            onClickGalleryItem(mediaPost, view)
            startStopSelecting()
        }
        super.onClickGalleryItem(mediaPost,view)
    }
    fun startStopSelecting(){
        var list= adapter.getItems()
        list.map {
            if(it.id==currentItemSelected!!.id){
                it.selectedPosition=1
            }
            it.selecting=!it.selecting
        }
        adapter.setGallery(list)
        adapter.notifyDataSetChanged()
    }
    override fun onClickGalleryItem(mediaPost: MediaPost, view: View) {
        if(isClicking||(selectedList.size>9&&!selectedList.contains(mediaPost))) return
        isClicking=true
        var list= adapter.getItems()
        if(currentItemSelected==null){
            currentItemSelected=mediaPost
        } else list.map { if(it.id==currentItemSelected!!.id)it.selected=false}
        currentItemSelected=mediaPost
        if(currentItemSelected?.selecting!!){
            var tempIds= arrayListOf<Long>()
            if(selectedList.contains(currentItemSelected!!)){
                selectedList.remove(currentItemSelected!!)
                for (i in 0 until selectedList.size){
                    selectedList[i].selectedPosition=i+1
                }
                if(selectedList.isNotEmpty())currentItemSelected=selectedList.last()
            }else{
                if(selectedList.isNotEmpty())selectedList.last().bitMap=cropView.output
                selectedList.add(currentItemSelected!!)
                selectedList.last().selectedPosition=selectedList.size
            }
            selectedList.map { tempIds.add(it.id!!) }
            list.map { if(tempIds.contains(it.id)){
                    it.selectedPosition=tempIds.lastIndexOf(it.id)+1
                }else{
                    it.selectedPosition=0
                }
            }
        }else{
            selectedList.clear()
            selectedList.add(currentItemSelected!!)
        }
        list.map { if(it==currentItemSelected){
            it.selected=true
            return@map
        }}
        if(selectedList.size>1){
            selectedList[selectedList.size-1].bitMap=cropView.output
        }
        loadImgPreview()
        adapter.setGallery(list)
        adapter.notifyDataSetChanged()
        isClicking=false
        super.onClickGalleryItem(mediaPost,view)
    }

}
