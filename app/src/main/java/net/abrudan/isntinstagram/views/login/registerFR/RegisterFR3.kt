package net.abrudan.isntinstagram.views.login.registerFR


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.android.synthetic.main.fragment_register_fr3.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.UserRepositories


class RegisterFR3 : Fragment() {
    private lateinit var userRepositories: UserRepositories
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_fr3, container, false)
    }
    override fun onStart() {
        super.onStart()
        userRepositories= UserRepositories()
        btnNext.setOnClickListener{onClickNext()}
    }

    private fun onClickNext(){
        Log.e("alex","_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-")
        userRepositories.setUserID(userName_field.text.toString()).addOnCompleteListener { task ->
            if(task.isSuccessful){
                var transaction= parentFragmentManager.beginTransaction()
                val newFragment= RegisterFR4()
                transaction.replace(R.id.LogInFR, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }else{
                Toast.makeText(context,"Ya existe!",Toast.LENGTH_SHORT).show()
                val e = task.exception
                if (e is FirebaseFunctionsException) {
                    Log.e("alex",e.code.toString())
                    Log.e("alex",e.details.toString())
                }
            }
        }

    }

}
