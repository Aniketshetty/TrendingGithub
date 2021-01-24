package com.mobile.trendinggit.Data

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface RepositoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun saveRepo(repository: List<Repository>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(repository: Repository?): Long

    @Query("SELECT * FROM repo ORDER BY id DESC")
    fun getAllRepo(): LiveData<List<Repository>>

    @Query("SELECT * FROM repo where id = :gitID  ORDER BY id DESC")
    fun gitRepoID(gitID: Int): Repository

}