package com.example.sitam.ui.auth

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sitam.SitamApplication
import com.example.sitam.models.auth.ResponseAuth
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class LoginViewModel(
    app: Application,
    private val sitamRepository: SitamRepository
) : AndroidViewModel(app) {

    val loginAuth: MutableLiveData<Resource<ResponseAuth>> = MutableLiveData()
    val regisAuth: MutableLiveData<Resource<ResponseAuth>> = MutableLiveData()

    fun loginUser(npm: String, password: String) = viewModelScope.launch {
        safeCallLogin(npm, password)
    }

    fun registerUser(
        npm: String,
        nama: String,
        noTelp: String,
        alamat: String,
        password: String,
        rePassword: String
    ) = viewModelScope.launch {
        safeCallRegister(npm, nama, noTelp, alamat, password, rePassword)
    }

    private fun handleLoginResponse(response: Response<ResponseAuth>): Resource<ResponseAuth>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleRegisterResponse(response: Response<ResponseAuth>): Resource<ResponseAuth>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallLogin(npm: String, password: String) {
        loginAuth.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.loginRequest(npm, password)
                loginAuth.postValue(handleLoginResponse(response))
            } else {
                loginAuth.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> loginAuth.postValue(Resource.Error("Network Failure"))
                else -> loginAuth.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeCallRegister(
        npm: String,
        nama: String,
        noTelp: String,
        alamat: String,
        password: String,
        rePassword: String
    ) {
        regisAuth.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response =
                    sitamRepository.registerRequest(npm, nama, noTelp, alamat, password, rePassword)
                regisAuth.postValue(handleRegisterResponse(response))
            } else {
                regisAuth.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> regisAuth.postValue(Resource.Error("Network Failure"))
                else -> regisAuth.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternectConnection(): Boolean {
        val connectivityManager = getApplication<SitamApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}
