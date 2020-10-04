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
import com.example.sitam.models.kolokium.ResponseReplyBimbinganKolokiumMhs
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class DetailBimbinganKolokiumMhsViewModel(app: Application): AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()

    val detailKolokium: LiveData<Resource<ResponseReplyBimbinganKolokiumMhs>>
        get() = _detailKolokium

    private val _detailKolokium: MutableLiveData<Resource<ResponseReplyBimbinganKolokiumMhs>> =
        MutableLiveData()

    fun getReplyBimbinganKolokium(token: String, idSeminar: String) = viewModelScope.launch {
        safeCallDetailBimbinganKolokium(token, idSeminar)
    }

    private fun handleReplyKolokiumResponse(response: Response<ResponseReplyBimbinganKolokiumMhs>): Resource<ResponseReplyBimbinganKolokiumMhs>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallDetailBimbinganKolokium(token: String, idSeminar: String) {
        _detailKolokium.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.replyBimbinganKolokiumMhs(token, idSeminar)
                _detailKolokium.postValue(handleReplyKolokiumResponse(response))
            } else {
                _detailKolokium.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _detailKolokium.postValue(Resource.Error("Network Failure"))
                else -> _detailKolokium.postValue(Resource.Error("Conversion Error"))
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