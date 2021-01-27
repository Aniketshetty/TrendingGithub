package com.mobile.trendinggit.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Repository::class], version = 1, exportSchema = false)
abstract class RepositoryDB : RoomDatabase() {

    abstract fun repoDAO(): RepositoryDAO

    companion object {
        private var INSTANCE: RepositoryDB? = null

        fun getInstance(context: Context): RepositoryDB? {
            if (INSTANCE == null) {
                synchronized(RepositoryDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        RepositoryDB::class.java,
                        "repoGit"
                    )
                        .build()
                }
            }
            return INSTANCE
        }
    }
}