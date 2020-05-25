package net.abrudan.isntinstagram

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import net.abrudan.isntinstagram.viewModel.GlobalViewModel


class MainActivity : AppCompatActivity() {
    private var permissionsRequired = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val PERMISSION_CALLBACK_CONSTANT = 100
    private val REQUEST_PERMISSION_SETTING = 101
    private var permissionStatus: SharedPreferences? = null
    private var sentToSettings = false
    private lateinit var navView:BottomNavigationView
    private lateinit var globalViewModel:GlobalViewModel
    private var auth= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        container.setBackgroundResource(R.color.colorPrimary)
        permissionStatus = getSharedPreferences("permissionStatus", Context.MODE_PRIVATE)
        requestPermission()
        navView= findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        navView.isSaveFromParentEnabled=true
        navView.isSaveEnabled=true
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.startFragment->hideNavBar()
                R.id.logInFR->hideNavBar()
                R.id.navigation_postcontent->hideNavBar()
                R.id.photoFragment->hideNavBar()
                R.id.registerFR1->hideNavBar()
                R.id.registerFR2->hideNavBar()
                R.id.registerFR3->hideNavBar()
                R.id.registerFR4->hideNavBar()
                R.id.uploadPostFragment->hideNavBar()
                R.id.editUserFragment->hideNavBar()
                else->showNavBar()
            }
        }
        globalViewModel= ViewModelProvider(this).get(GlobalViewModel::class.java)
        globalViewModel.getUploadingPost().observe(this, Observer {
            if(it==true){
                progressBar.visibility=View.VISIBLE
                tvUploading.visibility=View.VISIBLE
            }else if(it==false){
                progressBar.visibility=View.GONE
                tvUploading.visibility=View.GONE
            }else{
                Toast.makeText(this,"Error subiendo tu publicacion :(",Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun showNavBar(){
        navView.visibility= View.VISIBLE
    }
    private fun hideNavBar(){
        navView.visibility= View.GONE
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])) {
                //Show Information about why you need the permission
                getAlertDialog()
            } else if (permissionStatus!!.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission

            } else {
                //just request the permission
                ActivityCompat.requestPermissions(
                    this,
                    permissionsRequired,
                    PERMISSION_CALLBACK_CONSTANT
                )
            }

            //   txtPermissions.setText("Permissions Required")

            val editor = permissionStatus!!.edit()
            editor.putBoolean(permissionsRequired[0], true)
            editor.commit()
        } else {

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            var allgranted = false
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true
                } else {
                    allgranted = false
                    break
                }
            }
            if (allgranted) {

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])) {

                getAlertDialog()
            } else {
                finish()
            }
        }
    }

    private fun getAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Multiple Permissions")
        builder.setMessage("This app needs permissions.")
        builder.setPositiveButton("Grant") { dialog, which ->
            dialog.cancel()
            ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
            finish()
        }
        builder.show()
    }

    override fun onPostResume() {
        super.onPostResume()
        requestPermission()
    }
}
