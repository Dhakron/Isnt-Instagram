package net.abrudan.isntinstagram.views.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.abrudan.isntinstagram.R


// This is an arbitrary number we are using to keep track of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts.
private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)
private val REQUIRED_PERMISSIONS_GALLERY = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

class CameraActivity : AppCompatActivity() {

    private val transaction = supportFragmentManager.beginTransaction()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        if (!allPermissionsGranted()&&!allPermissionsGrantedStorage()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS_CAMERA, REQUEST_CODE_PERMISSIONS)
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS_GALLERY, REQUEST_CODE_PERMISSIONS)
        }
        val newFragment = CameraFR()
        transaction.replace(R.id.containerCamera, newFragment)
        transaction.commit()
    }




    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS_CAMERA.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    private fun allPermissionsGrantedStorage() = REQUIRED_PERMISSIONS_GALLERY.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


}
