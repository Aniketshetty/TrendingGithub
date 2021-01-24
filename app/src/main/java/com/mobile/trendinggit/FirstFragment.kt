package com.mobile.trendinggit

import android.app.Application
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.mobile.trendinggit.Utils.GitPeriodicSync
import com.mobile.trendinggit.Utils.ResponseStatus
import com.mobile.trendinggit.Utils.Status
import com.mobile.trendinggit.ViewModel.GitViewModel
import com.mobile.trendinggit.ViewModel.MainViewModelProviderFactory
import java.util.concurrent.TimeUnit
import androidx.work.Constraints;


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), GitListAdapter.GitEvents {
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var gitViewModel: GitViewModel
    private lateinit var gitListAdapter: GitListAdapter
    private var viewModelFactory: MainViewModelProviderFactory? = null
    private var mPeriodicWorkRequest: PeriodicWorkRequest? = null
    var progressDialog: ProgressDialog? = null
    var loadingDataDialog: Dialog? = null
    private lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        val myView = LayoutInflater.from(container!!.context)
            .inflate(R.layout.fragment_first, container, false)

        mainActivity = this.activity as MainActivity
        progressDialog = ProgressDialog(mainActivity)
        initializeLoading()
        mRecyclerView = myView!!.findViewById(R.id.rv_git_tm_list) as RecyclerView
        mLayoutManager = LinearLayoutManager(this.activity)
        mRecyclerView.layoutManager = mLayoutManager

        gitListAdapter = GitListAdapter(this)
        mRecyclerView.adapter = gitListAdapter
        getGitRepoStatus()
        periodicSync()
        return myView
    }

    private fun periodicSync() {
        // We can Cancel Periodic work by ID or TAG too
        val networkConstraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        mPeriodicWorkRequest = PeriodicWorkRequest.Builder(
            GitPeriodicSync::class.java,
            15, TimeUnit.MINUTES
        )
            .addTag("GitPeriodicSync")
            .setConstraints(networkConstraint)
            .build()

        WorkManager.getInstance().enqueue(mPeriodicWorkRequest!!)

    }


    private fun getGitRepoStatus() {

        gitViewModel.getGitRepoStatus(mainActivity)
            ?.observe(viewLifecycleOwner, Observer<ResponseStatus?> { it ->
                when (it?.status) {
                    Status.START -> {
                        showLoader()
                    }
                    Status.LOADING -> Log.d("MainActivity", " Current Status LOADING")
                    Status.SUCCESS -> {
                        closeLoader()
                    }
                    Status.ERROR -> {
                        Log.d(
                            "MainActivity",
                            " Current Status ERROR : " + it.data
                        )
                        closeLoader()
                    }
                }

            })
        gitViewModel.gitRepoData.observe(viewLifecycleOwner, Observer {
            gitListAdapter.setAllGitTMItems(it)
        })


    }
    //enable options menu in this fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModelFactory =
            MainViewModelProviderFactory(context.applicationContext as Application)
        gitViewModel =
            ViewModelProvider(this, viewModelFactory!!).get(GitViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.supportActionBar?.title = getString(R.string.app_name)
        gitViewModel.gitRepoData.observe(viewLifecycleOwner, Observer {
            gitListAdapter.setAllGitTMItems(it)
        })
    }

    override fun onGitClicked(repositoryID: Int) {
        var bundle = bundleOf(Constant.INTENT_OBJECT to repositoryID)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
    }

    private fun initializeLoading() {
        loadingDataDialog = Dialog(mainActivity)
        loadingDataDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDataDialog!!.setCancelable(false)
        loadingDataDialog!!.setContentView(R.layout.custom_loading_alert)
    }

    private fun showLoader() {
        loadingDataDialog?.show()
    }

    private fun closeLoader() {
        if (loadingDataDialog!!.isShowing) {
            loadingDataDialog?.dismiss()
        }
    }
}