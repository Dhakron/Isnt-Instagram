package net.abrudan.isntinstagram.views.login


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_log_in_fr.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.util.isEmailValid
import net.abrudan.isntinstagram.util.isPasswordValid
import net.abrudan.isntinstagram.views.login.registerFR.RegisterFR1
import net.abrudan.isntinstagram.views.main.MainActivity
import net.abrudan.isntinstagram.util.validateEmail
import net.abrudan.isntinstagram.util.validatePassword
class LogInFR : Fragment() {
    private var auth= FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_log_in_fr, container, false)
    }

    override fun onStart() {
        super.onStart()
        btnRegister.setOnClickListener{
            onClickRegister()
        }
        btnLogIn.setOnClickListener{
            onClickLogin()
        }
        user_field.doOnTextChanged{text, _, _, _ -> validateEmail(text.toString(),textInputLayout,btnLogIn,this.context!!)}
        password_field.doOnTextChanged{text, _, _, _ ->  validatePassword(text.toString(),textInputLayout2,btnLogIn,this.context!!)
        if (isEmailValid(user_field.text.toString())&& isPasswordValid(password_field.text.toString())) btnLogIn.isEnabled=true
        }
        btnLogIn.isEnabled=false
    }

    private fun onClickLogin(){
        log(user_field.text.toString(),password_field.text.toString())
    }

    private fun onClickRegister(){
        var transaction= parentFragmentManager.beginTransaction()
        val newFragment= RegisterFR1()
        transaction.replace(R.id.LogInFR, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun log(email:String,password:String){
        //fields
        user_field.isEnabled=false
        password_field.isEnabled=false
        //btns
        btnLogIn.isEnabled=false
        btnRegister.isEnabled=false
        btnLogIn.text=""
        Glide.with(this).load(R.drawable.load).into(ivLoad)
        ivLoad.forceHasOverlappingRendering(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this.context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    //fields
                    password_field.setText("")
                    password_field.isEnabled=true
                    user_field.isEnabled=true
                    //btns
                    btnLogIn.isEnabled=true
                    btnRegister.isEnabled=true
                    Glide.with(this).clear(ivLoad)
                    btnLogIn.text="Entrar"
                    Toast.makeText(context,"Usuario o contraseña incorrectos", Toast.LENGTH_SHORT)
                }
            }
    }

}
