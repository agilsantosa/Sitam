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

class EditProposalViewModel(app: Application) : AndroidViewModel(app){
    private val sitamRepository = SitamRepository()

    val editProposal: MutableLiveData<Resource<ResponseProposal>> = MutableLiveData()

    fun editProposal(
        token: String,
        idProposal: String,
        identifier: String,
        judulProposal: String,
        konsentrasi: String,
        topik: String
    ) = viewModelScope.launch {
        safeCallEditProposal(token, idProposal, identifier, judulProposal, konsentrasi, topik)
    }

    private fun handleEditProposalResponse(response: Response<ResponseProposal>): Resource<ResponseProposal> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Succes(response.message(), resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCallEditProposal(
        token: String,
        idProposal: String,
        identifier: String,
        judulProposal: String,
        konsentrasi: String,
        topik: String
    ) {
        editProposal.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.editProposal(
                    token,
                    idProposal,
                    identifier,
                    judulProposal,
                    konsentrasi,
                    topik
                )
                editProposal.postValue(handleEditProposalResponse(response))
            } else {
                editProposal.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> editProposal.postValue(Resource.Error("Network Failure"))
                else -> editProposal.postValue(Resource.Error("Conversion Error"))
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