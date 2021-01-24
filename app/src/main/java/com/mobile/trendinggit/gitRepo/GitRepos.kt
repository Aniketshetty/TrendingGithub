package com.mobile.trendinggit.gitRepo

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.trendinggit.Data.Repository
import com.mobile.trendinggit.Data.RepositoryDAO
import com.mobile.trendinggit.Data.RepositoryDB
import com.mobile.trendinggit.Model.RepositoryModel
import com.mobile.trendinggit.Network.APIClient
import com.mobile.trendinggit.Network.ApiInterface
import com.mobile.trendinggit.Utils.ResponseStatus
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.coroutines.CoroutineContext


class GitRepos(application: Application) {

    private var repositoryDAO: RepositoryDAO
    private var allRepository: LiveData<List<Repository>>

    private val responseStatus = MutableLiveData<ResponseStatus>()
    private val context1: CoroutineContext = Dispatchers.IO
    private val scope1 = CoroutineScope(context1 + SupervisorJob())
    var apiInterface: ApiInterface? = null
    var gitRepoListModel: RepositoryModel? = null

    var repoDB: RepositoryDB? = null

    init {
        repoDB = RepositoryDB.getInstance(application.applicationContext)
        repositoryDAO = repoDB!!.repoDAO()
        allRepository = repositoryDAO.getAllRepo()
        apiInterface = APIClient.client.create(ApiInterface::class.java)
    }

    fun saveGitTM(repository: List<Repository>?) = runBlocking {
        this.launch(Dispatchers.IO) {
            repositoryDAO.saveRepo(repository)
        }
    }

    fun getAllGitRepoList(): LiveData<List<Repository>> {
        return allRepository
    }

    fun getGitNetwork(context: Context): MutableLiveData<ResponseStatus> {
        responseStatus.value = ResponseStatus.start()

        if (getConnectivityStatusString(context)) {

            val call2: Call<RepositoryModel?> =
                apiInterface!!.fetchRepository("stars", "desc", 5)
            call2.enqueue(object : Callback<RepositoryModel?> {
                override fun onResponse(
                    call: Call<RepositoryModel?>,
                    response: Response<RepositoryModel?>
                ) {
                    scope1.launch {
                        var gitRepository: ArrayList<Repository>? = null
                        gitRepoListModel = response.body()
                        gitRepository = ArrayList()
//                        var image : Bitmap?=null
                        for (i in 1 until gitRepoListModel!!.items!!.size) {
//                            try {
//                                val url = URL( gitRepoListModel!!.items!!.get(i).owner!!.avatarUrl!!)
//                                image =
//                                    BitmapFactory.decodeStream(url.openConnection().getInputStream())
//                            } catch (e: IOException) {
//                                println(e)
//                            }
                            val repository = Repository(
                                i,
                                gitRepoListModel!!.items!!.get(i).fullName!!,
                                gitRepoListModel!!.items!!.get(i).name!!,
                                gitRepoListModel!!.items!!.get(i).owner!!.avatarUrl!!,
                                gitRepoListModel!!.items!!.get(i).description!!,
                                gitRepoListModel!!.items!!.get(i).language!!,
                                gitRepoListModel!!.items!!.get(i).stargazersCount,
                                gitRepoListModel!!.items!!.get(i).forksCount,
                                gitRepoListModel!!.items!!.get(i).defaultBranch!!
                            )
                            gitRepository!!.add(repository)
                        }
                        saveGitTM(gitRepository)
                        withContext(Dispatchers.Main)
                        {
                            responseStatus.value = ResponseStatus.success(gitRepository.toString())
                        }
                    }
                }

                override fun onFailure(
                    call: Call<RepositoryModel?>,
                    t: Throwable
                ) {
                    Toast.makeText(context, "onFailure", Toast.LENGTH_SHORT).show()
                    responseStatus.value = ResponseStatus.error(t)
                    call.cancel()
                }
            })
        } else {
            responseStatus.setValue(ResponseStatus.success("completed"))
        }
        return responseStatus
    }


    private fun getConnectivityStatusString(context: Context): Boolean {
        var status = true
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) {
            if (activeNetwork.type === ConnectivityManager.TYPE_WIFI) {
                status = true
                return status
            } else if (activeNetwork.type === ConnectivityManager.TYPE_MOBILE) {
                status =  true
                return status
            }
        } else {
            status = false
            return status
        }
        return status
    }

    fun gitRepoID(repositoryID: Int): Repository {
      return  repositoryDAO.gitRepoID(repositoryID)
    }
}