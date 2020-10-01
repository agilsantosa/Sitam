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
import com.example.sitam.models.ta.ResponseTugasAkhirMhs
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class TugasAkhirMahasiswaViewModel(app: Application) : AndroidViewModel(app) {

    private val sitamRepository = SitamRepository()

    val requestDataTaMhs: LiveData<Resource<ResponseTugasAkhirMhs>>
        get() = _requestDataTaMhs

    private val _requestDataTaMhs: MutableLiveData<Resource<ResponseTugasAkhirMhs>> =
        MutableLiveData()

    private val _status = MutableLiveData<String>()


    fun setStatus(status: String){
        _status.value = status
    }

    fun getStatus(): LiveData<String> = _status

    fun getDataTaMhs(token: String, identifier: String) = viewModelScope.launch {
        safeCallGetDataTa(token, identifier)
    }

    private fun handleGetDataTaMhs(response: Response<ResponseTugasAkhirMhs>): Resource<ResponseTugasAkhirMhs>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallGetDataTa(token: String, identifier: String){
        _requestDataTaMhs.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.requestTugasAkhirMhs(token, identifier)
                _requestDataTaMhs.postValue(handleGetDataTaMhs(response))
            } else {
                _requestDataTaMhs.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestDataTaMhs.postValue(Resource.Error("Network Failure"))
                else -> _requestDataTaMhs.postValue(Resource.Error("Conversion Error"))
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