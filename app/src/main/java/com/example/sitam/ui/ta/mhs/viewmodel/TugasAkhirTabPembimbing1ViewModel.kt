package com.example.sitam.ui.ta.mhs.viewmodel

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
import com.example.sitam.models.ta.ResponseListBimbinganTaMhs
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class TugasAkhirTabPembimbing1ViewModel(app: Application): AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()

    val requestListBimbinganTa: LiveData<Resource<ResponseListBimbinganTaMhs>>
        get() = _requestListBimbinganTa

    private val _requestListBimbinganTa: MutableLiveData<Resource<ResponseListBimbinganTaMhs>> = MutableLiveData()

    fun getListbBimbinganSeminar(token: String, idTa: String, to: String) = viewModelScope.launch {
        safeCallGetListBimbinganTa(token, idTa, to)
    }

    private fun handleGetListBimbinganTa(response: Response<ResponseListBimbinganTaMhs>): Resource<ResponseListBimbinganTaMhs>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallGetListBimbinganTa(token: String, idTa: String, to: String){
        _requestListBimbinganTa.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.requestListBimbinganTaMhs(token, idTa, to)
                _requestListBimbinganTa.postValue(handleGetListBimbinganTa(response))
            } else {
                _requestListBimbinganTa.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestListBimbinganTa.postValue(Resource.Error("Network Failure"))
                else -> _requestListBimbinganTa.postValue(Resource.Error("Conversion Error"))
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