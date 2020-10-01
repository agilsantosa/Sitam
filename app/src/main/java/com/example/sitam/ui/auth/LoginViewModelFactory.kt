package com.example.sitam.ui.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sitam.repository.SitamRepository

class LoginViewModelFactory(
    private val app: Application,
    private val sitamRepository: SitamRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(app, sitamRepository) as T
    }
}