package com.example.sitam.ui.proposal.dosen.viewmodel

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
import com.example.sitam.models.proposal.ResponseReplyProposal
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class DetailBimbinganProposalViewModel(app: Application) : AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()

    val replyProposal: LiveData<Resource<ResponseReplyProposal>>
        get() = _replyProposal

    private val _replyProposal: MutableLiveData<Resource<ResponseReplyProposal>> =
        MutableLiveData()

    fun getReplyBimbinganProposal(token: String, idProposal: String, by: String, catatan: String, status: String, idBimbingan: Int) = viewModelScope.launch {
        safeCallReplyBimbinganProposal(token, idProposal, by, catatan, status, idBimbingan)
    }

    private fun handleReplyProposalResponse(response: Response<ResponseReplyProposal>): Resource<ResponseReplyProposal>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallReplyBimbinganProposal(token: String, idProposal: String, by: String, catatan: String, status: String, idBimbingan: Int) {
        _replyProposal.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.replyBimbinganProposal(token, idProposal, by, catatan, status, idBimbingan)
                _replyProposal.postValue(handleReplyProposalResponse(response))
            } else {
                _replyProposal.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _replyProposal.postValue(Resource.Error("Network Failure"))
                else -> _replyProposal.postValue(Resource.Error("Conversion Error"))
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