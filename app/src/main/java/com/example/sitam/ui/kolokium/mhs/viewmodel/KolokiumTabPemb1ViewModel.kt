package com.example.sitam.ui.kolokium.mhs.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sitam.SitamApplication
import com.example.sitam.models.kolokium.ResponseListBimbinganKolokiumMhs
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class KolokiumTabPemb1ViewModel(app: Application) : AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()

    val requestListBimbinganKolokium: LiveData<Resource<ResponseListBimbinganKolokiumMhs>>
        get() = _requestListBimbinganKolokium

    private val _requestListBimbinganKolokium: MutableLiveData<Resource<ResponseListBimbinganKolokiumMhs>> = MutableLiveData()

    fun getListbBimbinganKolokium(token: String, idKolokium: String, to: String) = viewModelScope.launch {
        safeCallGetListBimbinganKolokium(token, idKolokium, to)
    }

    private fun handleGetListBimbinganKolokium(response: Response<ResponseListBimbinganKolokiumMhs>): Resource<ResponseListBimbinganKolokiumMhs>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallGetListBimbinganKolokium(token: String, idKolokium: String, to: String){
        _requestListBimbinganKolokium.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.requestListBimbinganKolokiumMhs(token, idKolokium, to)
                _requestListBimbinganKolokium.postValue(handleGetListBimbinganKolokium(response))
            } else {
                _requestListBimbinganKolokium.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestListBimbinganKolokium.postValue(Resource.Error("Network Failure"))
                else -> _requestListBimbinganKolokium.postValue(Resource.Error("Conversion Error"))
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