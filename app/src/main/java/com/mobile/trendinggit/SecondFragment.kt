package com.mobile.trendinggit

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mobile.trendinggit.Data.Repository
import com.mobile.trendinggit.ViewModel.GitViewModel
import com.mobile.trendinggit.ViewModel.MainViewModelProviderFactory
import kotlinx.android.synthetic.main.fragment_second.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var gitViewModel: GitViewModel
    private var viewModelFactory: MainViewModelProviderFactory? = null
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory =
                MainViewModelProviderFactory(context?.applicationContext as Application)
        gitViewModel =
                ViewModelProvider(this, viewModelFactory!!).get(GitViewModel::class.java)
        var repository: Repository? = null

        val repositoryID = arguments?.getInt(Constant.INTENT_OBJECT)
        repository = repositoryID?.let { gitViewModel.gitID(it as Int) }
        Glide.with(img_profile.context)
                .load(repository?.avatar)
                .error(R.drawable.baseline_account_circle_black_48)
                .into(img_profile)
        tv_name.text = "NAME : " + repository?.name
        tv_author.text = "Author Name : " + repository?.author
        tv_desc.text = "Description : " + repository?.description
        tv_lang.text = "Programming Lang : " + repository?.language
        tv_stars.text = "Stars : " + repository?.stars.toString()
        tv_forks.text = "Forks : " + repository?.forks.toString()
        tv_branch.text = "Branch : " + repository?.default_branch
    }
}