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
import com.example.sitam.models.ta.ResponseDataTugasAkhirDosen
import com.example.sitam.models.ta.ResponseListBimbinganTaMhs
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class TugasAkhirTabDosenViewModel(app: Application) : AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()

    val listTaMahasiswa: LiveData<Resource<ResponseDataTugasAkhirDosen>>
        get() = _listTaMahasiswa

    private val _listTaMahasiswa: MutableLiveData<Resource<ResponseDataTugasAkhirDosen>> =
        MutableLiveData()

    val listBimbinganTaMahasiswa: LiveData<Resource<ResponseListBimbinganTaMhs>>
        get() = _listBimbinganTaMahasiswa

    private val _listBimbinganTaMahasiswa: MutableLiveData<Resource<ResponseListBimbinganTaMhs>> =
        MutableLiveData()

    fun getTaMahasiswa(
        token: String,
        identifier: String
    ) = viewModelScope.launch {
        safeCallTaMahasiswa(token, identifier)
    }

    fun getBimbinganTaMahasiswa(
        token: String,
        idTa: String,
        alias: String
    ) = viewModelScope.launch {
        safeCallBimbinganTaMahasiswa(token, idTa, alias)
    }

    private fun handleTaResponse(response: Response<ResponseDataTugasAkhirDosen>): Resource<ResponseDataTugasAkhirDosen>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleBimbinganTaTaResponse(response: Response<ResponseListBimbinganTaMhs>): Resource<ResponseListBimbinganTaMhs>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallTaMahasiswa(token: String, identifier: String) {
        _listTaMahasiswa.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.listTaDosen(token, identifier)
                _listTaMahasiswa.postValue(handleTaResponse(response))
            } else {
                _listTaMahasiswa.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _listTaMahasiswa.postValue(Resource.Error("Network Failure"))
                else -> _listTaMahasiswa.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeCallBimbinganTaMahasiswa(token: String, idTa: String, alias: String) {
        _listBimbinganTaMahasiswa.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.requestListBimbinganTaDosen(token, idTa, alias)
                _listBimbinganTaMahasiswa.postValue(handleBimbinganTaTaResponse(response))
            } else {
                _listBimbinganTaMahasiswa.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _listBimbinganTaMahasiswa.postValue(Resource.Error("Network Failure"))
                else -> _listBimbinganTaMahasiswa.postValue(Resource.Error("Conversion Error"))
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