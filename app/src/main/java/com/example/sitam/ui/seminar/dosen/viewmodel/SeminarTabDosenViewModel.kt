package com.example.sitam.ui.seminar.dosen.viewmodel

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
import com.example.sitam.models.seminar.ResponseBimbinganSeminarDosen
import com.example.sitam.models.seminar.ResponseListBimbinganSeminarDosen
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class SeminarTabDosenViewModel(app: Application): AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()

    val requestBimbinganSeminarDosen: LiveData<Resource<ResponseBimbinganSeminarDosen>>
        get() = _requestBimbinganSeminarDosen

    private val _requestBimbinganSeminarDosen: MutableLiveData<Resource<ResponseBimbinganSeminarDosen>> = MutableLiveData()

    val requestListBimbinganSeminarDosen: LiveData<Resource<ResponseListBimbinganSeminarDosen>>
        get() = _requestListBimbinganSeminarDosen

    private val _requestListBimbinganSeminarDosen: MutableLiveData<Resource<ResponseListBimbinganSeminarDosen>> = MutableLiveData()

    fun getBimbinganSeminarDosen(token: String, identifier: String) = viewModelScope.launch {
        safeCallGetBimbinganSeminarDosen(token, identifier)
    }

    fun getListBimbinganSeminarDosen(token: String, idSeminar: String, alias: String) = viewModelScope.launch {
        safeCallGetListBimbinganSeminarDosen(token, idSeminar, alias)
    }

    private fun handleGetBimbinganSeminarDosen(response: Response<ResponseBimbinganSeminarDosen>): Resource<ResponseBimbinganSeminarDosen>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleGetListBimbinganSeminarDosen(response: Response<ResponseListBimbinganSeminarDosen>): Resource<ResponseListBimbinganSeminarDosen>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallGetBimbinganSeminarDosen(token: String, identifier: String){
        _requestBimbinganSeminarDosen.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.requestBimbinganSeminarDosen(token, identifier)
                _requestBimbinganSeminarDosen.postValue(handleGetBimbinganSeminarDosen(response))
            } else {
                _requestBimbinganSeminarDosen.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestBimbinganSeminarDosen.postValue(Resource.Error("Network Failure"))
                else -> _requestBimbinganSeminarDosen.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeCallGetListBimbinganSeminarDosen(token: String, idSeminar: String, alias: String){
        _requestListBimbinganSeminarDosen.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.requestListBimbinganSeminarDosen(token, idSeminar, alias)
                _requestListBimbinganSeminarDosen.postValue(handleGetListBimbinganSeminarDosen(response))
            } else {
                _requestListBimbinganSeminarDosen.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestListBimbinganSeminarDosen.postValue(Resource.Error("Network Failure"))
                else -> _requestListBimbinganSeminarDosen.postValue(Resource.Error("Conversion Error"))
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