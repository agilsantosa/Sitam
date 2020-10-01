package com.example.sitam.ui.proposal.mhs.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import com.example.sitam.SitamApplication
import com.example.sitam.models.proposal.ResponseBimbinganProposal
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class DetailProposalViewModel(val app: Application) : AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()

    val _listBimbinganProposal: LiveData<Resource<ResponseBimbinganProposal>>
        get() = listBimbinganProposal

    private val listBimbinganProposal: MutableLiveData<Resource<ResponseBimbinganProposal>> = MutableLiveData()

    fun getListBimbinganProposal(
        token: String,
        idProposal: Int
    ) = viewModelScope.launch {
        safeCallListBimbinganProposal(token, idProposal)
    }

    private fun handleProposalResponse(response: Response<ResponseBimbinganProposal>): Resource<ResponseBimbinganProposal>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallListBimbinganProposal(
        token: String,
        idProposal: Int
    ) {
        listBimbinganProposal.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.listBimbinganProposal(token, idProposal)
                listBimbinganProposal.postValue(handleProposalResponse(response))
            } else {
                listBimbinganProposal.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> listBimbinganProposal.postValue(Resource.Error("Network Failure"))
                else -> listBimbinganProposal.postValue(Resource.Error("Conversion Error"))
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