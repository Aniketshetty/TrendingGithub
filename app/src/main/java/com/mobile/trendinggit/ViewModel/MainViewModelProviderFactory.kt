package com.mobile.trendinggit.ViewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelProviderFactory(private val mDataSource: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GitViewModel::class.java)) {
            return GitViewModel(mDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}