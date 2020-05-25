package net.abrudan.isntinstagram.views.main.user

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_edit_user.*
import kotlinx.android.synthetic.main.fragment_edit_user.ivThumb
import kotlinx.android.synthetic.main.fragment_edit_user.progressBar
import kotlinx.android.synthetic.main.fragment_edit_user.userNameInputLayout
import kotlinx.android.synthetic.main.fragment_edit_user.userName_field
import kotlinx.android.synthetic.main.fragment_log_in_fr.*
import kotlinx.android.synthetic.main.fragment_profile_photo_set_view.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.model.Repositories
import net.abrudan.isntinstagram.model.User
import net.abrudan.isntinstagram.util.validateUserName
import net.abrudan.isntinstagram.viewModel.GlobalViewModel

/**
 * A simple [Fragment] subclass.
 */
class EditUserFragment : Fragment() {
    private var updatingName=false
    private var updatingBio=false
    private var updatingUserName=false
    private lateinit var repositories:Repositories
    private lateinit var globalViewModel: GlobalViewModel
    private lateinit var currentData: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_user, container, false)
    }

    override fun onStart() {
        super.onStart()
        globalViewModel= ViewModelProvider(activity!!).get(GlobalViewModel::class.java)
        repositories= Repositories()
        globalViewModel.getMyUserData().observe(viewLifecycleOwner, Observer {
            Picasso.get().load(it!!.profileImgURI).placeholder(R.drawable.ic_userdefault).into(ivThumb)
            email_field.text=Editable.Factory.getInstance().newEditable(FirebaseAuth.getInstance().currentUser!!.email)
            userName_field.text=Editable.Factory.getInstance().newEditable(it!!.userID)
            name_field.text=Editable.Factory.getInstance().newEditable(it.name)
            bio_field.text=Editable.Factory.getInstance().newEditable(it.bio)
            currentData=it
        })
        userName_field.doOnTextChanged { text, _, _, _ ->
            btn_accept.isEnabled= validateUserName(text.toString(), userNameInputLayout, btn_accept, this.context!!)
            updatingUserName=false
        }
        name_field.doOnTextChanged { text, _, _, _ ->
            updatingName=false
        }
        bio_field.doOnTextChanged { text, _, _, _ ->
            updatingUserName=false
        }
        btn_accept.setOnClickListener{
            checkData()
        }
        ivThumb.setOnClickListener{
            findNavController().navigate(EditUserFragmentDirections.actionGlobalPhotoFragment(true))
        }
        tvEditProfileImage.setOnClickListener {
            findNavController().navigate(EditUserFragmentDirections.actionGlobalPhotoFragment(true))
        }
        ivBack.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
    fun checkData(){
        if(currentData.name != name_field.text.toString() && !updatingName){
            updatingName=true
        }
        if(currentData.userID != userName_field.text.toString()){
            updatingUserName=true
        }
        if(currentData.bio != bio_field.text.toString() && !updatingBio){
            updatingBio=true
        }
        updateData()
    }
    fun updateData(){
        if(updatingName||updatingBio||updatingUserName){
            bio_field.isEnabled=false
            name_field.isEnabled=false
            userName_field.isEnabled=false
            btn_accept.isEnabled=false
            btn_accept.text=""
            ivThumb.isEnabled=false
            tvEditProfileImage.isEnabled=false
            ivBack.isEnabled=false
            progressBar.visibility=View.VISIBLE
            repositories.editUserData(name_field.text.toString(),bio_field.text.toString()).addOnCompleteListener{
                if(it.isSuccessful){
                    updatingName=false
                    updatingBio=false
                    if(!updatingName&&!updatingUserName){
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }else{
                    Toast.makeText(context,"Error actualizando su nombre o presentacion",Toast.LENGTH_SHORT).show()
                    bio_field.isEnabled=true
                    name_field.isEnabled=true
                    userName_field.isEnabled=true
                    btn_accept.isEnabled=true
                    btn_accept.text=getString(R.string.btn_accept)
                    ivThumb.isEnabled=true
                    tvEditProfileImage.isEnabled=true
                    ivBack.isEnabled=true
                    progressBar.visibility=View.GONE
                }
            }
            if(updatingUserName){
                Log.e("sssss","ASsssssssdflkasdhfkjashdjklfasldgfhdfsssssssss")
                repositories.setUserID(userName_field.text.toString()).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        updatingUserName=false
                        if(!updatingName&&!updatingUserName){
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    } else{
                        bio_field.isEnabled=true
                        name_field.isEnabled=true
                        userName_field.isEnabled=true
                        btn_accept.isEnabled=true
                        btn_accept.text=getString(R.string.btn_accept)
                        ivThumb.isEnabled=true
                        tvEditProfileImage.isEnabled=true
                        ivBack.isEnabled=true
                        progressBar.visibility=View.GONE
                    }
            }
        }
    }else{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}
