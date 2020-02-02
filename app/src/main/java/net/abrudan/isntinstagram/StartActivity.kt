package net.abrudan.isntinstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.abrudan.isntinstagram.views.login.LogInActivity
import net.abrudan.isntinstagram.views.main.MainActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}
