package net.abrudan.isntinstagram.views.login.registerFR

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_register_fr2.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.util.isPasswordValid
import net.abrudan.isntinstagram.util.validatePassword
import net.abrudan.isntinstagram.util.validateRePassword
import org.jetbrains.anko.support.v4.find

class RegisterFR2 : Fragment() {
    private val auth=FirebaseAuth.getInstance()
    private var email: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_fr2, container, false)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString("email")
        }
    }
    override fun onStart() {
        super.onStart()
        progressBar.visibility=View.GONE
        btnNext.isEnabled=false
        password_field.doOnTextChanged { text, _, _, _ ->
            btnNext.isEnabled= validatePassword(text.toString(), textInputLayout, btnNext, this.context!!) && validateRePassword(text.toString(),password_field2.text.toString(), textInputLayout1, btnNext, this.context!!)
        }
        password_field2.doOnTextChanged { text, _, _, _ ->
            btnNext.isEnabled= (isPasswordValid(text.toString()) && validateRePassword(text.toString(),password_field.text.toString(), textInputLayout1, btnNext, this.context!!))
        }
        btnNext.setOnClickListener { onClickNext() }
    }
    private fun onClickNext(){
        progressBar.visibility=View.VISIBLE
        //fields
        password_field.isEnabled=false
        password_field2.isEnabled=false
        //btns
        btnNext.isEnabled=false
        btnNext.text=""
        auth.createUserWithEmailAndPassword(email?:"error", password_field2.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(RegisterFR2Directions.actionRegisterFR2ToRegisterFR3())
                } else {
                    Toast.makeText(context,getString(R.string.err_register), Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }

    }

}
