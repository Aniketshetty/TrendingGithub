package com.mobile.trendinggit.Network

import com.mobile.trendinggit.Model.RepositoryModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterface {
    @GET("search/repositories?q=android%20language:java&")
    fun fetchRepository(
        @Query("sort") sort: String?,
        @Query("order") order: String?,
        @Query("page") page: Int?
    ): Call<RepositoryModel?>
}
