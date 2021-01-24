package com.mobile.trendinggit.ViewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mobile.trendinggit.Data.Repository
import com.mobile.trendinggit.Utils.ResponseStatus
import com.mobile.trendinggit.gitRepo.GitRepos


class GitViewModel(application: Application) : AndroidViewModel(application) {

    private var gitStatus: LiveData<ResponseStatus>? = null
    var gitRepoData: LiveData<List<Repository>>
    lateinit var gitRepoID: Repository
    private val gitRepo: GitRepos = GitRepos(application)


    init {
        gitRepoData = gitRepo.getAllGitRepoList()
    }



    fun getGitRepoStatus(context: Context): LiveData<ResponseStatus>? {
        gitStatus = gitRepo.getGitNetwork(context)
        return gitStatus
    }

    fun gitID(repositoryID: Int): Repository {
        gitRepoID = gitRepo.gitRepoID(repositoryID)
        return gitRepoID
    }

}