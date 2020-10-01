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
import com.example.sitam.models.kolokium.ResponseDataBimbinganKolokiumDosen
import com.example.sitam.models.kolokium.ResponseListBimbinganKolokiumDosen
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class KolokiumTabDosenViewModel(app: Application): AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()

    val requestBimbinganKolokiumDosen: LiveData<Resource<ResponseDataBimbinganKolokiumDosen>>
        get() = _requestBimbinganKolokiumDosen

    private val _requestBimbinganKolokiumDosen: MutableLiveData<Resource<ResponseDataBimbinganKolokiumDosen>> =
        MutableLiveData()

    val requestListBimbinganKolokiumDosen: LiveData<Resource<ResponseListBimbinganKolokiumDosen>>
        get() = _requestListBimbinganKolokiumDosen

    private val _requestListBimbinganKolokiumDosen: MutableLiveData<Resource<ResponseListBimbinganKolokiumDosen>> = MutableLiveData()

    fun getBimbinganSeminarDosen(token: String, identifier: String) = viewModelScope.launch {
        safeCallGetBimbinganKolokiumDosen(token, identifier)
    }

    fun getListBimbinganKolokiumDosen(token: String, idKolokium: String, alias: String) = viewModelScope.launch {
        safeCallGetListBimbinganKolokiumDosen(token, idKolokium, alias)
    }

    private fun handleGetBimbinganKolokiumDosen(response: Response<ResponseDataBimbinganKolokiumDosen>): Resource<ResponseDataBimbinganKolokiumDosen>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleGetListBimbinganKolokiumDosen(response: Response<ResponseListBimbinganKolokiumDosen>): Resource<ResponseListBimbinganKolokiumDosen>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallGetBimbinganKolokiumDosen(token: String, identifier: String){
        _requestBimbinganKolokiumDosen.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.requestBimbinganKolokiumDosen(token, identifier)
                _requestBimbinganKolokiumDosen.postValue(handleGetBimbinganKolokiumDosen(response))
            } else {
                _requestBimbinganKolokiumDosen.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestBimbinganKolokiumDosen.postValue(Resource.Error("Network Failure"))
                else -> _requestBimbinganKolokiumDosen.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeCallGetListBimbinganKolokiumDosen(token: String, idKolokium: String, alias: String){
        _requestListBimbinganKolokiumDosen.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.requestListBimbinganKolokiumDosen(token, idKolokium, alias)
                _requestListBimbinganKolokiumDosen.postValue(handleGetListBimbinganKolokiumDosen(response))
            } else {
                _requestListBimbinganKolokiumDosen.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestListBimbinganKolokiumDosen.postValue(Resource.Error("Network Failure"))
                else -> _requestListBimbinganKolokiumDosen.postValue(Resource.Error("Conversion Error"))
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
