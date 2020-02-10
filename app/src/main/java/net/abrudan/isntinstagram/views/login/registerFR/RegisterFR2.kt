package net.abrudan.isntinstagram.views.login.registerFR

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_register_fr2.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.util.isPasswordValid
import net.abrudan.isntinstagram.util.validatePassword
import net.abrudan.isntinstagram.util.validateRePassword

private const val ARG_Email = "ARG_Email"
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
            email = it.getString(ARG_Email)
            Log.e("error", "=================================================================")
            Log.e("error", email)
        }
    }
    override fun onStart() {
        super.onStart()
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
        //fields
        password_field.isEnabled=false
        password_field2.isEnabled=false
        //btns
        btnNext.isEnabled=false
        btnNext.text=""
        Glide.with(this).load(R.drawable.load).into(ivLoad)
        ivLoad.forceHasOverlappingRendering(true)
        auth.createUserWithEmailAndPassword(email?:"error", password_field2.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var transaction= parentFragmentManager.beginTransaction()
                    val newFragment= RegisterFR3()
                    transaction.replace(R.id.LogInFR, newFragment)
                    transaction.commit()
                } else {
                    Toast.makeText(context,"Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                    var transaction= parentFragmentManager.beginTransaction()
                    val newFragment= RegisterFR1()
                    transaction.replace(R.id.LogInFR, newFragment)
                    transaction.commit()
                }
            }

    }

}
