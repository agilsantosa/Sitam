package com.example.sitam.ui.chat

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sitam.SitamApplication
import com.example.sitam.models.pesan.ResponseChat
import com.example.sitam.models.profile.ResponseProfileMhs
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class ChatViewModel(
    app: Application
) : AndroidViewModel(app) {
    val pesanMahasiswa: MutableLiveData<Resource<ResponseChat>> = MutableLiveData()

    val pesanDosen: MutableLiveData<Resource<ResponseChat>> = MutableLiveData()
    private val sitamRepository = SitamRepository()

//    init {
//        when {
//            level == "mahasiswa" -> getPesanMahasiswa(token, identifier)
//            level == "dosen" -> getPesanDosen(token, identifier)
//        }
//    }

    fun getPesanMahasiswa(token: String, identifier: String) = viewModelScope.launch {
        safeCallGetPesan(token, identifier)
    }

    fun getPesanDosen(token: String, identifier: String) = viewModelScope.launch {
        safeCallGetPesanDosen(token, identifier)
    }

    private fun handlePesanMhsResponse(response: Response<ResponseChat>): Resource<ResponseChat>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handlePesanDsnResponse(response: Response<ResponseChat>): Resource<ResponseChat>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallGetPesan(token: String, identifier: String) {
        pesanMahasiswa.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.chatMahasiswaRequest(token, identifier)
                pesanMahasiswa.postValue(handlePesanMhsResponse(response))
            } else {
                pesanMahasiswa.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> pesanMahasiswa.postValue(Resource.Error("Network Failure"))
                else -> pesanMahasiswa.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeCallGetPesanDosen(token: String, identifier: String) {
        pesanDosen.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.chatDosenRequest(token, identifier)
                pesanDosen.postValue(handlePesanDsnResponse(response))
            } else {
                pesanDosen.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> pesanDosen.postValue(Resource.Error("Network Failure"))
                else -> pesanDosen.postValue(Resource.Error("Conversion Error"))
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