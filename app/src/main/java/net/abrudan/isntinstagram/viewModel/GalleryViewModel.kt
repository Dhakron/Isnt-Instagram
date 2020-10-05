package net.abrudan.isntinstagram.viewModel

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.abrudan.isntinstagram.model.MediaPost
import org.jetbrains.anko.doAsync


class GalleryViewModel : ViewModel() {
    private val TAG = "Gallery-View-Model"
    private var tGalleryList = mutableListOf<MediaPost>()
    private var galleryList : MutableLiveData<List<MediaPost>> = MutableLiveData()
    private var albumList : MutableLiveData<List<String?>> = MutableLiveData()
    private var lastMediaLoaded=0
    private var pageSize=100
    private var loadingData=false
    fun loadAllMedia(context: Context):MutableLiveData<List<MediaPost>>{
        doAsync {
            tGalleryList.clear()
            getAllImg(context)
            setAllbumsList()
            loadMediaByAlbum("Gallery",false)
        }
        return galleryList
    }
    fun restarLastMediaLoaded(){
        lastMediaLoaded=0
    }
    fun loadingData():Boolean{
        return loadingData
    }
    fun loadMediaByAlbum(album:String,selecting:Boolean){
        doAsync {
            loadingData=true
            var temporalList = when(album){
                "Gallery"->{
                    tGalleryList.distinctBy { it.imgURI }.sortedBy { it.id }.reversed()
                }
                "Images"->{
                    tGalleryList.distinctBy { it.id }.filter { it.type.equals("img") }.sortedBy { it.id }.reversed()
                }
                "Video"->{
                    tGalleryList.distinctBy { it.id }.filter { it.folderName.equals("video") }.sortedBy { it.id }.reversed()
                }
                else->{
                    tGalleryList.distinctBy { it.id }.filter { it.folderName==album }.sortedBy { it.id }.reversed()
                }
            }
            if(temporalList.size<=lastMediaLoaded+pageSize){
                temporalList.subList(0,temporalList.size).map { it.selecting=selecting }
                galleryList.postValue(temporalList.subList(0,temporalList.size))
                lastMediaLoaded=temporalList.size
            }else{
                temporalList.subList(0,lastMediaLoaded+pageSize).map { it.selecting=selecting }
                galleryList.postValue(temporalList.subList(0,lastMediaLoaded+pageSize))
                lastMediaLoaded+=pageSize
            }
            loadingData=false
        }
    }

    fun loadAllAlbums():MutableLiveData<List<String?>>{
        return albumList
    }

    fun setAllbumsList(){
        albumList.postValue(tGalleryList.map{ it?.folderName }.distinct().filterNotNull())
    }

    fun getAllVideo(context: Context){
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        )
        val sortOrder = "${MediaStore.Video.Media._ID} ASC"
        val query = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder,
            null
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val folderColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val folderName = cursor.getString(folderColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                tGalleryList.add(MediaPost(contentUri,name,id,folderName,null,"video"))
            }
        }
    }

    fun getAllImg(context: Context){
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            val sortOrder = "${MediaStore.Images.Media._ID} ASC"
            val query = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder,
                null
            )
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val folderColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val folderName = cursor.getString(folderColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    tGalleryList.add(MediaPost(contentUri,name,id,folderName,null,"img"))
                }
            }
            query?.close()
    }
}