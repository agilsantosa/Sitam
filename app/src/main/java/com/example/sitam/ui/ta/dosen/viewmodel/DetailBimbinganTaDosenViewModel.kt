package com.example.sitam.ui.ta.dosen.viewmodel

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
import com.example.sitam.models.ta.ResponseReplyTa
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class DetailBimbinganTaDosenViewModel(app: Application) : AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()

    val replyTugasAkhir: LiveData<Resource<ResponseReplyTa>>
        get() = _replyTugasAkhir

    private val _replyTugasAkhir: MutableLiveData<Resource<ResponseReplyTa>> =
        MutableLiveData()

    fun getReplyBimbinganTugasAkhir(token: String, idTa: String, by: String, catatan: String, status: String, idBimbingan: Int) = viewModelScope.launch {
        safeCallReplyBimbinganTa(token, idTa, by, catatan, status, idBimbingan)
    }

    private fun handleReplyTaResponse(response: Response<ResponseReplyTa>): Resource<ResponseReplyTa>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallReplyBimbinganTa(token: String, idTa: String, by: String, catatan: String, status: String, idBimbingan: Int) {
        _replyTugasAkhir.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.replyBimbinganTa(token, idTa, by, catatan, status, idBimbingan)
                _replyTugasAkhir.postValue(handleReplyTaResponse(response))
            } else {
                _replyTugasAkhir.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _replyTugasAkhir.postValue(Resource.Error("Network Failure"))
                else -> _replyTugasAkhir.postValue(Resource.Error("Conversion Error"))
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