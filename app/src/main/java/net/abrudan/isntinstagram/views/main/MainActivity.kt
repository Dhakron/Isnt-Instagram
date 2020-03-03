package net.abrudan.isntinstagram.views.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.views.camera.CameraActivity
import net.abrudan.isntinstagram.views.login.LogInActivity

class MainActivity : AppCompatActivity() {
    private var auth= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,R.id.navigation_search,
                R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_user
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        btnSalir.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, LogInActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        btnPrueba.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }
    fun onClickFollowers(uid:String){
    }
    fun onClickFollowing(v:View){

    }
}
