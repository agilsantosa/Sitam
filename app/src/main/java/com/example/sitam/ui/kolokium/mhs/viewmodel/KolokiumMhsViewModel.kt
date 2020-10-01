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
import com.example.sitam.models.kolokium.ResponseDataKolokiumMhs
import com.example.sitam.models.kolokium.ResponseRegisterKolokiumMhs
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class KolokiumMhsViewModel(app: Application): AndroidViewModel(app) {

    private val sitamRepository = SitamRepository()


    val requestDataKolokium: LiveData<Resource<ResponseDataKolokiumMhs>>
        get() = _requestDataKolokium

    private val _requestDataKolokium: MutableLiveData<Resource<ResponseDataKolokiumMhs>> = MutableLiveData()

    val requestDaftarKolokium: LiveData<Resource<ResponseRegisterKolokiumMhs>>
        get() = _requestDaftarKolokium

    private val _requestDaftarKolokium: MutableLiveData<Resource<ResponseRegisterKolokiumMhs>> = MutableLiveData()

    private val _status = MutableLiveData<String>()

    fun setStatus(status: String){
        _status.value = status
    }

    fun getStatus(): LiveData<String> = _status

    fun getDataKolokium(token: String, identifier: String) = viewModelScope.launch {
        safeCallGetDataKolokium(token, identifier)
    }

    fun getDaftarKolokium(token: String, identifier: String, idProposal: String) = viewModelScope.launch {
        safeCallGetDaftarKolokium(token, identifier, idProposal)
    }

    private fun handleGetDataKolokium(response: Response<ResponseDataKolokiumMhs>): Resource<ResponseDataKolokiumMhs>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleGetDaftarKolokium(response: Response<ResponseRegisterKolokiumMhs>): Resource<ResponseRegisterKolokiumMhs>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallGetDaftarKolokium(token: String, identifier: String, idProposal: String){
        _requestDaftarKolokium.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.reqestDaftarKolokium(token, identifier, idProposal)
                _requestDaftarKolokium.postValue(handleGetDaftarKolokium(response))
            } else {
                _requestDaftarKolokium.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestDaftarKolokium.postValue(Resource.Error("Network Failure"))
                else -> _requestDaftarKolokium.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeCallGetDataKolokium(token: String, identifier: String){
        _requestDataKolokium.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.requestDataKolokiumMhs(token, identifier)
                _requestDataKolokium.postValue(handleGetDataKolokium(response))
            } else {
                _requestDataKolokium.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestDataKolokium.postValue(Resource.Error("Network Failure"))
                else -> _requestDataKolokium.postValue(Resource.Error("Conversion Error"))
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