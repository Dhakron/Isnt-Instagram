package net.abrudan.isntinstagram.views.login.registerFR
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_register_fr4.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.views.camera.CameraActivity
import net.abrudan.isntinstagram.MainActivity

class RegisterFR4 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_register_fr4, container, false)
    }
    override fun onStart() {
        super.onStart()
        btnSkip.setOnClickListener{skip()}
        btnAdd.setOnClickListener{openCamera()}
    }

    private fun skip() {
        findNavController().navigate(RegisterFR4Directions.actionGlobalNavigationHome())
    }

    private fun openCamera(){
        findNavController().navigate(RegisterFR4Directions.actionGlobalPhotoFragment(true))
    }

}
