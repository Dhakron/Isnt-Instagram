package net.abrudan.isntinstagram.views.login.registerFR

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_register_fr1.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.util.validateEmail


class RegisterFR1 : Fragment() {
    private val auth= FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_fr1, container, false)
    }

    override fun onStart() {
        super.onStart()
        progressBar.visibility=View.GONE
        btnNext.isEnabled=false
        email_field.doOnTextChanged{text, _, _, _ ->
            btnNext.isEnabled = validateEmail(text.toString(),textInputLayout,btnNext,this.context!!)
        }
        btnNext.setOnClickListener{onClickNext(email_field.text.toString())}
    }
    private fun onClickNext(email:String){
        progressBar.visibility=View.VISIBLE
        //fields
        email_field.isEnabled=false
        //btns
        btnNext.isEnabled=false
        btnNext.text=""
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener{ task ->
                if (task.result!!.signInMethods!!.isEmpty()) {
                    findNavController().navigate(RegisterFR1Directions.actionRegisterFR1ToRegisterFR2(email_field.text.toString()))
                } else {
                    progressBar.visibility=View.GONE
                    textInputLayout.error=getString(R.string.err_msg_email_used)
                    //fields
                    email_field.isEnabled=true
                    //btns
                    btnNext.text=getString(R.string.btn_next)
                }
            }
    }

}

