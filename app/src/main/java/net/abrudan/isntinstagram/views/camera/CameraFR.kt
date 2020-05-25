package net.abrudan.isntinstagram.views.camera
import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_camera_fr.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.MediaPost
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


private const val REQUEST_CODE_STORAGE = 100

class CameraFR : Fragment() {
    private var lensFacing = CameraX.LensFacing.FRONT

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera_fr, container, false)
    }

    override fun onStart() {
        super.onStart()

        viewFinder.post { startCamera() }
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _->
            updateTransform()
        }
        btnSwitchCamera.setOnClickListener{switchCamera()}
        btnGallery.setOnClickListener{openGallery()}
    }
    /**
     * Open gallery for pick a image and get it
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_STORAGE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_STORAGE){
            data?.data?.let { uri->
                launchPhotoPreView(uri)
            }
        }else{
            Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Camera start and switch
    */
    private fun switchCamera() {
        lensFacing = if(lensFacing==CameraX.LensFacing.BACK){
            CameraX.LensFacing.FRONT
        }else{
            CameraX.LensFacing.BACK
        }
        startCamera()
    }

    private val executor = Executors.newSingleThreadExecutor()

    private fun startCamera() {
        CameraX.unbindAll()
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setLensFacing(lensFacing)
        }.build()

        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)
            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        // Create configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                setLensFacing(lensFacing)
            }.build()

        // Build the image capture use case and attach button click listener
        val imageCapture = ImageCapture(imageCaptureConfig)

        captureBtn.setOnClickListener {

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
                        launchPhotoPreView(file.toUri())
                    }
                })
        }
        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

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

    private fun launchPhotoPreView(uri: Uri) {
        val image=uri.toString()
        val bundle = Bundle()
        bundle.putString("ARG_Image",image)
        bundle.putBoolean("ARG_Front", lensFacing==CameraX.LensFacing.FRONT)
        var transaction= parentFragmentManager.beginTransaction()
        val newFragment= ProfilePhotoSet()
        newFragment.arguments=bundle
        transaction.replace(R.id.containerCamera, newFragment).addToBackStack(null)
        transaction.commit()
    }

}
