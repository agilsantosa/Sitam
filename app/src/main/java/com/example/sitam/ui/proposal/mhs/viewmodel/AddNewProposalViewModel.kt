package com.example.sitam.ui.proposal.mhs.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sitam.SitamApplication
import com.example.sitam.models.proposal.ResponseProposal
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class AddNewProposalViewModel(app: Application) : AndroidViewModel(app) {

    private val sitamRepository = SitamRepository()

    val addProposal: MutableLiveData<Resource<ResponseProposal>> = MutableLiveData()

    fun addNewProposal(
        token: String,
        identifier: String,
        judulProposal: String,
        konsentrasi: String,
        topik: String
    ) = viewModelScope.launch {
        safeCallAddProposal(token, identifier, judulProposal, konsentrasi, topik)
    }

    private fun handleAddProposalResponse(response: Response<ResponseProposal>): Resource<ResponseProposal> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Succes(response.message(), resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallAddProposal(
        token: String,
        identifier: String,
        judulProposal: String,
        konsentrasi: String,
        topik: String
    ) {
        addProposal.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.addNewProposal(
                    token,
                    identifier,
                    judulProposal,
                    konsentrasi,
                    topik
                )
                addProposal.postValue(handleAddProposalResponse(response))
            } else {
                addProposal.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> addProposal.postValue(Resource.Error("Network Failure"))
                else -> addProposal.postValue(Resource.Error("Conversion Error"))
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