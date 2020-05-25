package net.abrudan.isntinstagram.views.login


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_log_in_fr.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.util.isEmailValid
import net.abrudan.isntinstagram.util.isPasswordValid
import net.abrudan.isntinstagram.views.login.registerFR.RegisterFR1
import net.abrudan.isntinstagram.MainActivity
import net.abrudan.isntinstagram.model.Repositories
import net.abrudan.isntinstagram.util.validateEmail
import net.abrudan.isntinstagram.util.validatePassword
import net.abrudan.isntinstagram.views.main.StartFragmentDirections

class LogInFR : Fragment() {
    private var auth= FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_log_in_fr, container, false)
    }
    private lateinit var repositories:Repositories

    override fun onStart() {
        super.onStart()
        repositories= Repositories()
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
        progressBar.visibility=View.GONE
        btnLogIn.isEnabled=false
    }

    private fun onClickLogin(){
        log(user_field.text.toString(),password_field.text.toString())
    }

    private fun onClickRegister(){
        findNavController().navigate(LogInFRDirections.actionLogInFRToRegisterFR1())
    }

    private fun log(email:String,password:String){
        progressBar.visibility=View.VISIBLE
        //fields
        user_field.isEnabled=false
        password_field.isEnabled=false
        //btns
        btnLogIn.isEnabled=false
        btnLogIn.text=""
        btnRegister.isEnabled=false
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val user=FirebaseAuth.getInstance().currentUser
                if (task.isSuccessful) {
                    repositories.getUserInfo(user!!.uid).addOnSuccessListener {
                        var userInfo= (it.toObject(net.abrudan.isntinstagram.model.UserInfo::class.java))
                        if(userInfo?.userID==null){
                            findNavController().navigate(LogInFRDirections.actionLogInFRToRegisterFR3())
                        }else{
                            findNavController().navigate(LogInFRDirections.actionGlobalNavigationHome())
                        }
                        }
                } else {
                    progressBar.visibility=View.GONE
                    //fields
                    password_field.setText("")
                    password_field.isEnabled=true
                    textInputLayout2.error=getString(R.string.err_msg_login)
                    user_field.isEnabled=true
                    //btns
                    btnLogIn.isEnabled=false
                    btnLogIn.text=getString(R.string.btn_next)
                    btnRegister.isEnabled=true

                }
            }
    }
}
