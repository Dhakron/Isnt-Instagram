package net.abrudan.isntinstagram.views.main.addPost

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_upload_post.*

import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.UploadPostTabAdapter
import net.abrudan.isntinstagram.model.MediaPost
import net.abrudan.isntinstagram.viewModel.GalleryViewModel
import net.abrudan.isntinstagram.viewModel.GlobalViewModel
import net.abrudan.isntinstagram.viewModel.UploadPostViewModel


class UploadPostFragment : Fragment() {
    private val adapter by lazy { UploadPostTabAdapter(this.activity!!,imageList) }
    private lateinit var imageList:List<MediaPost>
    private lateinit var globalViewModel: GlobalViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_post, container, false)
    }

    override fun onStart() {
        super.onStart()
        arguments?.let {
            imageList= (it.get("imageList") as Array<MediaPost>).toList()
        }
        if (vpImagePreview.adapter == null) {
            vpImagePreview.adapter = adapter
        }
        globalViewModel= ViewModelProvider(activity!!).get(GlobalViewModel::class.java)
        btnPost.setOnClickListener{
            globalViewModel.uploadMedia(imageList,etTittlePost.text.toString())
            findNavController().navigate(UploadPostFragmentDirections.actionGlobalNavigationHome())
        }
        ivBack.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}
