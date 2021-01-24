package com.mobile.trendinggit.Data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repo")
data class Repository(
    @field:PrimaryKey(autoGenerate = true)
    val id: Int,
    val author: String,
    val name: String,
    val avatar: String,
    val description: String,
    val language: String? = "language",
    val stars: Int,
    val forks: Int,
    val default_branch: String
)