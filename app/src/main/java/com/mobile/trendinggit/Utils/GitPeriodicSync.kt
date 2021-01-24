package com.mobile.trendinggit.Utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mobile.trendinggit.Data.Repository
import com.mobile.trendinggit.Data.RepositoryDB
import com.mobile.trendinggit.Model.RepositoryModel
import com.mobile.trendinggit.Network.APIClient
import com.mobile.trendinggit.Network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class GitPeriodicSync(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    var apiInterface: ApiInterface? = null
    var gitRepoListModel: RepositoryModel? = null

    init {
        apiInterface = APIClient.client.create(ApiInterface::class.java)
    }

    override fun doWork(): Result {
        val call2: Call<RepositoryModel?> =
            apiInterface!!.fetchRepository("stars", "desc", 5)
        call2.enqueue(object : Callback<RepositoryModel?> {
            override fun onResponse(
                call: Call<RepositoryModel?>,
                response: Response<RepositoryModel?>
            ) {
                var gitRepository: ArrayList<Repository>? = null
                gitRepoListModel = response.body()
                gitRepository = ArrayList()
                for (i in 1 until gitRepoListModel!!.items!!.size) {
                    val repository = Repository(
                        i,
                        gitRepoListModel!!.items!![i].fullName!!,
                        gitRepoListModel!!.items!![i].name!!,
                        gitRepoListModel!!.items!![i].owner!!.avatarUrl!!,
                        gitRepoListModel!!.items!![i].description!!,
                        gitRepoListModel!!.items!![i].language!!,
                        gitRepoListModel!!.items!![i].stargazersCount,
                        gitRepoListModel!!.items!![i].forksCount,
                        gitRepoListModel!!.items!![i].defaultBranch!!
                    )
                    gitRepository.add(repository)
                }
                RepositoryDB.getInstance(applicationContext)!!.repoDAO().saveRepo(gitRepository)
            }

            override fun onFailure(
                call: Call<RepositoryModel?>,
                t: Throwable
            ) {
                Toast.makeText(applicationContext, "onFailure", Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })

        return Result.success()
    }

    companion object {
        private val TAB = GitPeriodicSync::class.java.simpleName
    }
}