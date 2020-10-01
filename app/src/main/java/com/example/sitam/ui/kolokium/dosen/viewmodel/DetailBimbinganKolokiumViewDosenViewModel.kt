package com.example.sitam.ui.kolokium.dosen.viewmodel

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
import com.example.sitam.models.seminar.ResponseSeminarReply
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class DetailBimbinganKolokiumViewDosenViewModel(app: Application) : AndroidViewModel(app)  {

    private val sitamRepository = SitamRepository()

    val replyKolokium: LiveData<Resource<ResponseSeminarReply>>
        get() = _replyKolokium

    private val _replyKolokium: MutableLiveData<Resource<ResponseSeminarReply>> =
        MutableLiveData()

    fun getReplyBimbinganKolokium(token: String, idKolokium: String, status: String, by: String, idBimbingan: Int, catatan: String, nilai: String? ) = viewModelScope.launch {
        safeCallReplyBimbinganKolokium(token, idKolokium, status, by, idBimbingan, catatan, nilai)
    }

    private fun handleReplyKolokiumResponse(response: Response<ResponseSeminarReply>): Resource<ResponseSeminarReply>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallReplyBimbinganKolokium(token: String, idKolokium: String, status: String, by: String, idBimbingan: Int, catatan: String, nilai: String? ) {
        _replyKolokium.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.replyBimbinganKolokium(token, idKolokium, status, by, idBimbingan, catatan, nilai)
                _replyKolokium.postValue(handleReplyKolokiumResponse(response))
            } else {
                _replyKolokium.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _replyKolokium.postValue(Resource.Error("Network Failure"))
                else -> _replyKolokium.postValue(Resource.Error("Conversion Error"))
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