package net.abrudan.isntinstagram.views.login.registerFR
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register_fr4.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.views.camera.CameraActivity
import net.abrudan.isntinstagram.views.main.MainActivity

class RegisterFR4 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_fr4, container, false)
    }
    override fun onStart() {
        super.onStart()
        btnSkip.setOnClickListener{skip()}
        btnAdd.setOnClickListener{openCamera()}
    }

    private fun skip() {
        val intent = Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun openCamera(){
        val intent = Intent(context, CameraActivity::class.java)
        startActivity(intent)
    }

}
