package net.abrudan.isntinstagram.util

import android.content.Context
import android.content.res.Resources
import android.widget.Button
import com.google.android.material.textfield.TextInputLayout
import net.abrudan.isntinstagram.R

fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
fun isPasswordValid(password: String):Boolean{
    return password.length>5
}

fun validateEmail(email: String, inputLayout: TextInputLayout, btn : Button, context: Context): Boolean {
    if(email.isEmpty()){
        inputLayout.isErrorEnabled = false
        btn.isEnabled=false
    }else if (!isEmailValid(email)) {
        inputLayout.error = context.getString(R.string.err_msg_email)
        btn.isEnabled=false
        return false
    } else {
        inputLayout.isErrorEnabled = false
    }
    return true
}

fun validatePassword(password: String, inputLayout: TextInputLayout, btn : Button, context: Context): Boolean {
    if(password.isEmpty()){
        inputLayout.isErrorEnabled = false
        btn.isEnabled=false
    }else if (!isPasswordValid(password)) {
        inputLayout.error = context.getString(R.string.err_msg_password)
        btn.isEnabled=false
        return false
    } else {
        inputLayout.isErrorEnabled = false
    }
    return true
}
fun validateRePassword(password: String,rePassword: String, inputLayout: TextInputLayout, btn : Button, context: Context): Boolean {
    if(password != rePassword){
        inputLayout.error = context.getString(R.string.err_msg_rePassword)
        btn.isEnabled=false
        return false
    } else {
        inputLayout.isErrorEnabled = false
    }
    return true
}