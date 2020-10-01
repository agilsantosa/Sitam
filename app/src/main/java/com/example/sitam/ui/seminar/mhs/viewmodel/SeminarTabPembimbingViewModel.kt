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
import com.example.sitam.models.seminar.ResponseBimbinganSeminar
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class SeminarTabPembimbingViewModel(app: Application): AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()

    val requestListBimbinganSeminar: LiveData<Resource<ResponseBimbinganSeminar>>
        get() = _requestListBimbinganSeminar

    private val _requestListBimbinganSeminar: MutableLiveData<Resource<ResponseBimbinganSeminar>> = MutableLiveData()

    fun getListbBimbinganSeminar(token: String, idSeminar: String, to: String) = viewModelScope.launch {
        safeCallGetListBimbinganSeminar(token, idSeminar, to)
    }

    private fun handleGetListBimbinganSeminar(response: Response<ResponseBimbinganSeminar>): Resource<ResponseBimbinganSeminar>?{
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallGetListBimbinganSeminar(token: String, idSeminar: String, to: String){
        _requestListBimbinganSeminar.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.requestListBimbinganSeminarMhs(token, idSeminar, to)
                _requestListBimbinganSeminar.postValue(handleGetListBimbinganSeminar(response))
            } else {
                _requestListBimbinganSeminar.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _requestListBimbinganSeminar.postValue(Resource.Error("Network Failure"))
                else -> _requestListBimbinganSeminar.postValue(Resource.Error("Conversion Error"))
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