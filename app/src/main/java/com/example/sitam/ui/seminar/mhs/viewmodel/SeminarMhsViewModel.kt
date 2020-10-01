package com.example.sitam.ui.seminar.mhs.viewmodel

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
import com.example.sitam.models.seminar.ResponseDaftarSeminar
import com.example.sitam.models.seminar.ResponseSeminarMhs
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class SeminarMhsViewModel(app: Application): AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()

    val requestDataSeminar: LiveData<Resource<ResponseSeminarMhs>>
        get() = _requestDataSeminar

    private val _requestDataSeminar: MutableLiveData<Resource<ResponseSeminarMhs>> = MutableLiveData()

    val requestDaftarSeminar: LiveData<Resource<ResponseDaftarSeminar>>
        get() = _requestDaftarSeminar

    private val _requestDaftarSeminar: MutableLiveData<Resource<ResponseDaftarSeminar>> = MutableLiveData()


    private val _status = MutableLiveData<String>()

    fun setStatus(status: String){
        _status.value = status
    }

    fun getStatus(): LiveData<String> = _status


    fun getDataSeminar(token: String, identifier: String) = viewModelScope.launch {
        safeCallGetDataSeminar(token, identifier)
    }

    fun getDaftarSeminar(token: String, identifier: String, idProposal: String) = viewModelScope.launch {
        safeCallGetDaftarSeminar(token, identifier, idProposal)
    }



    private fun handleGetDataSeminar(response: Response<ResponseSeminarMhs>): Resource<ResponseSeminarMhs>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleGetDaftarSeminar(response: Response<ResponseDaftarSeminar>): Resource<ResponseDaftarSeminar>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }



    private suspend fun safeCallGetDataSeminar(token: String, identifier: String){
        _requestDataSeminar.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.getSeminar(token, identifier)
                _requestDataSeminar.postValue(handleGetDataSeminar(response))
            } else {
                _requestDataSeminar.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestDataSeminar.postValue(Resource.Error("Network Failure"))
                else -> _requestDataSeminar.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeCallGetDaftarSeminar(token: String, identifier: String, idProposal: String){
        _requestDaftarSeminar.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.requestDaftarSeminar(token, identifier, idProposal)
                _requestDaftarSeminar.postValue(handleGetDaftarSeminar(response))
            } else {
                _requestDaftarSeminar.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestDaftarSeminar.postValue(Resource.Error("Network Failure"))
                else -> _requestDaftarSeminar.postValue(Resource.Error("Conversion Error"))
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