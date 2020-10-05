package net.abrudan.isntinstagram.views.main.search

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_search.*
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.adapters.SearchViewAdapter
import net.abrudan.isntinstagram.model.UserInfo
import net.abrudan.isntinstagram.viewModel.SearchViewModel


class SearchFragment : Fragment(),SearchView.OnQueryTextListener,SearchViewAdapter.SearchViewAdapterInterface {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchView:androidx.appcompat.widget.SearchView
    private lateinit var adapter: SearchViewAdapter
    private var searching=false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        initRV()
        searchView= svSearch as SearchView
        searchView.queryHint = getString(R.string.topBarItem_msg_search)
        searchView.setOnQueryTextListener(this)
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        searchViewModel.getSearchUsers().observe(this, Observer { postView->
            adapter.setUserInfo(postView)
            srLayout.isRefreshing=false
            progressBar.visibility=View.GONE
            })
        searchViewModel.getUsers().observe(this, Observer { postView->
            postView?.let {
                adapter.setUserInfo(it.sortedBy { it!!.date }.reversed())
                srLayout.isRefreshing=false
                progressBar.visibility=View.GONE
            } })
        searchViewModel.loadAllUsers()
        srLayout.setOnRefreshListener {
            searching=false
            searchViewModel.loadAllUsers()
        }
    }

    private fun initRV() {
        adapter= SearchViewAdapter(context!!,R.layout.row_search,this)
        rvSearch.adapter=adapter
        rvSearch.layoutManager= LinearLayoutManager(context)
        rvSearch.addOnScrollListener(
            object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!searchViewModel.loadingData&&dy>0&&recyclerView.layoutManager?.childCount!! +
                        (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        >= recyclerView.layoutManager?.itemCount!! - 2) {
                        if(searching){
                            searchViewModel.searchUserFrom()
                        }else{
                            searchViewModel.loadAllUsersFrom()
                        }
                        progressBar.visibility=View.VISIBLE
                    }
                }
            }
        )
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText!=null){
            searching=true
            searchViewModel.searchUser(newText)
        }else{
            searching=false
        }
        return true
    }

    override fun click(user: UserInfo) {
        findNavController().navigate(SearchFragmentDirections.actionGlobalUserFragment(user.userUID?:""))
        super.click(user)
    }
}