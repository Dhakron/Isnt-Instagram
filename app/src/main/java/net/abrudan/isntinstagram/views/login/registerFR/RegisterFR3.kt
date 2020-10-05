package net.abrudan.isntinstagram.views.login.registerFR


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.android.synthetic.main.fragment_register_fr3.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.Repositories
import net.abrudan.isntinstagram.util.DialogExitSetUserID
import net.abrudan.isntinstagram.util.validateUserName


class RegisterFR3 : Fragment() {
    private lateinit var userRepositories: Repositories
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_fr3, container, false)
    }
    override fun onStart() {
        super.onStart()
        progressBar.visibility=View.GONE
        userRepositories= Repositories()
        btnNext.setOnClickListener{onClickNext()}
        userName_field.doOnTextChanged { text, _, _, _ ->
            btnNext.isEnabled= validateUserName(text.toString(), userNameInputLayout, btnNext, this.context!!)
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fm: FragmentManager = requireActivity().supportFragmentManager
                var s= DialogExitSetUserID()
                DialogExitSetUserID().show(fm,"")


            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun onClickNext(){
        progressBar.visibility=View.VISIBLE
        btnNext.isEnabled=false
        btnNext.text=""
        userName_field.isEnabled=false
        userRepositories.setUserID(userName_field.text.toString()).addOnCompleteListener { task ->
            if(task.isSuccessful){
                findNavController().navigate(RegisterFR3Directions.actionRegisterFR3ToRegisterFR4())
            }else{
                userName_field.isEnabled=true
                btnNext.isEnabled=false
                progressBar.visibility=View.GONE
                btnNext.text=getString(R.string.btn_next)
                userNameInputLayout.error=getString(R.string.err_msg_username_exist)
                val e = task.exception
                if (e is FirebaseFunctionsException) {
                    Log.e("Error",e.code.toString())
                    Log.e("Error",e.details.toString())
                }
            }
        }
    }
}
