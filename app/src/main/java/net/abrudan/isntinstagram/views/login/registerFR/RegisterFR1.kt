package net.abrudan.isntinstagram.views.login.registerFR

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_fr1, container, false)
    }

    override fun onStart() {
        super.onStart()
        btnNext.isEnabled=false
        email_field.doOnTextChanged{text, _, _, _ ->
            btnNext.isEnabled = validateEmail(text.toString(),textInputLayout,btnNext,this.context!!)
        }
        btnNext.setOnClickListener{onClickNext(email_field.text.toString())}
    }
    private fun onClickNext(email:String){
        //fields
        email_field.isEnabled=false
        //btns
        btnNext.isEnabled=false
        btnNext.text=""
        Glide.with(this).load(R.drawable.load).into(ivLoad)
        ivLoad.forceHasOverlappingRendering(true)
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener{ task ->
                if (task.result!!.signInMethods!!.isEmpty()) {
                    val bundle = Bundle()
                    bundle.putString("ARG_Email",email_field.text.toString())
                    var transaction= parentFragmentManager.beginTransaction()
                    val newFragment= RegisterFR2()
                    newFragment.arguments=bundle
                    transaction.replace(R.id.LogInFR, newFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                } else {
                    Toast.makeText(context,"Este correo ya esta en uso", Toast.LENGTH_SHORT).show()
                    //fields
                    email_field.isEnabled=true
                    //btns
                    btnNext.text=getString(R.string.btn_next)
                    Glide.with(this).clear(ivLoad)
                }
            }
    }

}

