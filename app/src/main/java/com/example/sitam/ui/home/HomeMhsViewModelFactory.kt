package com.example.sitam.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sitam.repository.SitamRepository

class HomeMhsViewModelFactory(
    private val app: Application,
    private val sitamRepository: SitamRepository,
    private val token: String,
    private val identifier: String,
    private val level: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeMhsViewModel(app, sitamRepository, token, identifier, level) as T
    }
}