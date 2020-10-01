package com.example.sitam.ui.home

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import com.example.sitam.SitamApplication
import com.example.sitam.models.auth.ResponseAuth
import com.example.sitam.models.profile.ResponseProfileDosen
import com.example.sitam.models.profile.ResponseProfileMhs
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class HomeMhsViewModel(
    app: Application,
    private val sitamRepository: SitamRepository,
    token: String,
    identifier: String,
    level: String
) : AndroidViewModel(app) {

    val profileMahasiswa: MutableLiveData<Resource<ResponseProfileMhs>> = MutableLiveData()
    val profileDosen: MutableLiveData<Resource<ResponseProfileDosen>> = MutableLiveData()

    init {
        when {
            level == "mahasiswa" -> getProfileMahasiswa(token, identifier)
            level == "dosen" -> getProfileDosen(token, identifier)
        }

    }

    fun getProfileMahasiswa(token: String, identifier: String) = viewModelScope.launch {
        safeCallGetProfile(token, identifier)
    }

    fun getProfileDosen(token: String, identifier: String) = viewModelScope.launch {
        safeCallGetProfileDosen(token, identifier)
    }

    private fun handleProfileMhsResponse(response: Response<ResponseProfileMhs>): Resource<ResponseProfileMhs>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleProfileDsnResponse(response: Response<ResponseProfileDosen>): Resource<ResponseProfileDosen>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallGetProfile(token: String, identifier: String) {
        profileMahasiswa.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.profileUserRequest(token, identifier)
                profileMahasiswa.postValue(handleProfileMhsResponse(response))
            } else {
                profileMahasiswa.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> profileMahasiswa.postValue(Resource.Error("Network Failure"))
                else -> profileMahasiswa.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeCallGetProfileDosen(token: String, identifier: String) {
        profileDosen.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.profileDosenRequest(token, identifier)
                profileDosen.postValue(handleProfileDsnResponse(response))
            } else {
                profileDosen.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> profileDosen.postValue(Resource.Error("Network Failure"))
                else -> profileDosen.postValue(Resource.Error("Conversion Error"))
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
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}