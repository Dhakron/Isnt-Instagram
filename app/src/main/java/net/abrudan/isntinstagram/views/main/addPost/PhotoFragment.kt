package net.abrudan.isntinstagram.views.main.addPost

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.camera.core.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_follows.*
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.android.synthetic.main.fragment_photo.cropView
import kotlinx.android.synthetic.main.fragment_profile_photo_set_view.*

import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.MediaPost
import net.abrudan.isntinstagram.model.Repositories
import net.abrudan.isntinstagram.util.flip
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread
import java.io.File
import java.util.concurrent.Executors

/**
 * A simple [Fragment] subclass.
 */
class PhotoFragment : Fragment() {
    private var cameraHeight=0
    private var cameraWidth=0
    private lateinit var imageCapture:ImageCapture
    private val executor = Executors.newSingleThreadExecutor()
    private var lensFacing = CameraX.LensFacing.FRONT
    private var enableTourch = false
    private var viewPreview=false
    private var forProfileImage=false
    private var repository=Repositories()
    private var fromGalery=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }
    override fun onStart() {
        super.onStart()
        arguments?.let {
            forProfileImage=it.getBoolean("forProfileImage")
        }

        if(enableTourch){
            btnFlash.setBackgroundResource(R.drawable.ic_flash_on)
        }
        else btnFlash.setBackgroundResource(R.drawable.ic_flash_off)
        btnSwitchCamera.setBackgroundResource(R.drawable.ic_switchcamera)
        btnSwitchCamera.z = 10F
        captureBtn.setBackgroundResource(R.drawable.ic_interface)
        cameraWidth=context!!.displayMetrics.widthPixels
        cameraHeight=context!!.displayMetrics.widthPixels+(context!!.displayMetrics.widthPixels/4)
        cameraLayout.layoutParams=LinearLayout.LayoutParams(cameraWidth,cameraHeight)
        previewLayout.layoutParams=LinearLayout.LayoutParams(cameraWidth,cameraHeight)
        vfCamera.post { startCamera(vfCamera,captureBtn) }
        btnSwitchCamera.setOnClickListener{switchCamera()}
        btnFlash.setOnClickListener{
            if(lensFacing==CameraX.LensFacing.FRONT){
                return@setOnClickListener
            }
            enableTourch=!enableTourch
            if(enableTourch)btnFlash.setBackgroundResource(R.drawable.ic_flash_on)
            else btnFlash.setBackgroundResource(R.drawable.ic_flash_off)
            startCamera(vfCamera,captureBtn)
        }
        if(forProfileImage){
            btnNext.text=getString(R.string.btn_accept)
        }
        ivGallery.setOnClickListener {
            fromGalery=true
            openGallery()
        }
        btnNext.setOnClickListener{
            var list:MutableList<MediaPost> = mutableListOf()
            val bitMapFile= if(lensFacing==CameraX.LensFacing.FRONT&&!fromGalery)cropView.output.flip() else cropView.output
            var mediaPost=MediaPost(type = "img",bitMap = bitMapFile)
            list.add(mediaPost)
            if(forProfileImage){
                btnNext.isEnabled=false
                captureBtn.isEnabled=false
                ivBack.isEnabled=false
                progressBar.visibility=View.VISIBLE
                repository.uploadImageProfile(mediaPost.bitMap!!).addOnSuccessListener {
                    findNavController().navigate(PhotoFragmentDirections.actionGlobalStartFragment())
                }.addOnFailureListener{
                    btnNext.isEnabled=true
                    captureBtn.isEnabled=true
                    ivBack.isEnabled=true
                    progressBar.visibility=View.GONE

                }
            }else{
                findNavController().navigate(PhotoFragmentDirections.actionGlobalUploadPostFragment(list.toTypedArray()))
            }
        }
        ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    fun loadPreview(uri: Uri){
        ivGallery.visibility=View.GONE
        captureBtn.setBackgroundResource(R.drawable.ic_photocancel)
        viewPreview=true
        if(forProfileImage){
            cropView.of(uri).withAspect(1,1).initialize(context)
        }else{
            cropView.of(uri).withAspect(4,3).initialize(context)
        }
        if(lensFacing==CameraX.LensFacing.FRONT&&!fromGalery)cropView.scaleX=-1.0F
        cameraLayout.visibility=ConstraintLayout.GONE
        previewLayout.visibility=ConstraintLayout.VISIBLE
        btnNext.visibility=Button.VISIBLE
    }
    fun loadCamera(){
        fromGalery=false
        ivGallery.visibility=View.VISIBLE
        captureBtn.setBackgroundResource(R.drawable.ic_interface)
        viewPreview=false
        cameraLayout.visibility=ConstraintLayout.VISIBLE
        previewLayout.visibility=ConstraintLayout.GONE
        btnNext.visibility=Button.GONE
    }

    override fun onResume() {
        super.onResume()
        if(!fromGalery){
            vfCamera.post { startCamera(vfCamera,captureBtn) }
            loadCamera()
        }
    }

    private fun switchCamera() {
        if(lensFacing==CameraX.LensFacing.BACK){
            enableTourch=false
            btnFlash.setBackgroundResource(R.drawable.ic_flash_off)
            lensFacing = CameraX.LensFacing.FRONT
        }else{
            lensFacing = CameraX.LensFacing.BACK
        }
        startCamera(vfCamera,captureBtn)
    }

    private fun startCamera(viewFinder: TextureView,captureBtn: ImageView) {
        CameraX.unbindAll()
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_4_3)
            setLensFacing(lensFacing)
        }.build()

        // Build the viewfinder use case
        val preview = Preview(previewConfig)
        //preview.enableTorch(enableTourch)
        preview.setOnPreviewOutputUpdateListener {
           if(viewFinder!=null){
               val parent = viewFinder.parent as ViewGroup
               parent.removeView(viewFinder)
               parent.addView(viewFinder, 0)
               viewFinder.surfaceTexture = it.surfaceTexture
               updateTransform(viewFinder)
           }
        }

        // Create configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                setLensFacing(lensFacing)
                setFlashMode(if(enableTourch)FlashMode.ON else FlashMode.OFF)
            }.build()

        // Build the image capture use case and attach button click listener
        imageCapture = ImageCapture(imageCaptureConfig)

        captureBtn.setOnClickListener {
            if(viewPreview){
                loadCamera()
            }else{
                val file = File(context!!.externalMediaDirs.first(),
                    "${System.currentTimeMillis()}.jpg")
                imageCapture.takePicture(file,executor,
                    object : ImageCapture.OnImageSavedListener {
                        override fun onError(
                            imageCaptureError: ImageCapture.ImageCaptureError,
                            message: String,
                            exc: Throwable?
                        ) {
                            val msg = "Photo capture failed: $message"
                            Log.e("CameraXApp", msg, exc)
                            viewFinder.post {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onImageSaved(file: File) {
                            this@PhotoFragment.runOnUiThread {
                                loadPreview(file.toUri())
                            }
                        }
                    })
            }

        }
        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    private fun updateTransform(viewFinder: TextureView) {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f
        if(viewFinder.display!=null){
            // Correct preview output to account for display rotation
            val rotationDegrees = when (viewFinder.display.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> return
            }
            matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
            // Finally, apply transformations to our TextureView
            viewFinder.setTransform(matrix)
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100){
            data?.data?.let { uri->

                loadPreview(uri)
            }
        }else{
        }
    }
}
