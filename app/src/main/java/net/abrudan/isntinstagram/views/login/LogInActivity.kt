package net.abrudan.isntinstagram.views.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.abrudan.isntinstagram.R

class LogInActivity : AppCompatActivity(){
        private val transaction = supportFragmentManager.beginTransaction()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        val newFragment = LogInFR()
        transaction.replace(R.id.LogInFR, newFragment)
        transaction.commit()
    }

}
