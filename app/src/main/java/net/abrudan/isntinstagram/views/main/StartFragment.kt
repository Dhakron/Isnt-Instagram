package net.abrudan.isntinstagram.views.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.Repositories
import net.abrudan.isntinstagram.viewModel.GlobalViewModel
import org.jetbrains.anko.doAsync

/**
 * A simple [Fragment] subclass.
 */
class StartFragment : Fragment() {
    private lateinit var globalViewModel:GlobalViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }
    private lateinit var repositories: Repositories
    override fun onStart() {
        super.onStart()
        globalViewModel= ViewModelProvider(activity!!).get(GlobalViewModel::class.java)
        repositories= Repositories()
        checkCurrentUser()

    }
    private fun checkCurrentUser(){
        val user = FirebaseAuth.getInstance().currentUser
        if (user== null) {
            findNavController().navigate(StartFragmentDirections.actionStartFragmentToLogInFR())
        } else {
            repositories.getUserInfo(user.uid).addOnSuccessListener {
                var userInfo= (it.toObject(net.abrudan.isntinstagram.model.UserInfo::class.java))
                if(userInfo?.userID==null){
                    findNavController().navigate(StartFragmentDirections.actionStartFragmentToRegisterFR3())
                }else{
                    globalViewModel.loadMyuserData()
                    findNavController().navigate(StartFragmentDirections.actionStartFragmentToNavigationHome())
                }
            }.addOnFailureListener{
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(StartFragmentDirections.actionStartFragmentToLogInFR())
            }
        }
    }
}
