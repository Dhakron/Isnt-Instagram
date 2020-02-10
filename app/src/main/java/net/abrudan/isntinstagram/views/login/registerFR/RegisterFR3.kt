package net.abrudan.isntinstagram.views.login.registerFR

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register_fr3.*

import net.abrudan.isntinstagram.R


class RegisterFR3 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_fr3, container, false)
    }
    override fun onStart() {
        super.onStart()
        btnNext.setOnClickListener{onClickNext()}
        btnChange.setOnClickListener{onClickChange()}
    }

    private fun onClickChange() {
        var transaction= parentFragmentManager.beginTransaction()
        val newFragment= RegisterFR3_2()
        transaction.replace(R.id.LogInFR, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun onClickNext(){
        var transaction= parentFragmentManager.beginTransaction()
        val newFragment= RegisterFR4()
        transaction.replace(R.id.LogInFR, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
