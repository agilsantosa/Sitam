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
import com.example.sitam.models.proposal.ResponseBimbinganProposal
import com.example.sitam.models.proposal.ResponseProposalDosen
import com.example.sitam.repository.SitamRepository
import com.example.sitam.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class ProposalTabViewModel(app: Application) : AndroidViewModel(app) {
    private val sitamRepository = SitamRepository()
    
    val listProposalMahasiswa: LiveData<Resource<ResponseProposalDosen>>
        get() = _listProposalMahasiswa

    private val _listProposalMahasiswa: MutableLiveData<Resource<ResponseProposalDosen>> =
        MutableLiveData()

    val listBimbinganProposalMahasiswa: LiveData<Resource<ResponseBimbinganProposal>>
        get() = _listBimbinganProposalMahasiswa

    private val _listBimbinganProposalMahasiswa: MutableLiveData<Resource<ResponseBimbinganProposal>> =
        MutableLiveData()

    fun getProposalMahasiswa(
        token: String,
        identifier: String,
        level: String
    ) = viewModelScope.launch {
        safeCallProposalMahasiswa(token, identifier, level)
    }

    fun getListBimbinganProposal(token: String, identifier: String, idProposal: Int) = viewModelScope.launch {
        safeCallBimbinganProposalMahasiswa(token, identifier, idProposal)
    }

    private fun handleProposalResponse(response: Response<ResponseProposalDosen>): Resource<ResponseProposalDosen>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleBimbinganProposalResponse(response: Response<ResponseBimbinganProposal>): Resource<ResponseBimbinganProposal>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Succes(response.message(), it)
            }
        }
        return Resource.Error(response.message())
    }


    private suspend fun safeCallProposalMahasiswa(token: String, identifier: String, level: String) {
        _listProposalMahasiswa.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.listProposalDosen(token, identifier, level)
                _listProposalMahasiswa.postValue(handleProposalResponse(response))
            } else {
                _listProposalMahasiswa.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _listProposalMahasiswa.postValue(Resource.Error("Network Failure"))
                else -> _listProposalMahasiswa.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeCallBimbinganProposalMahasiswa(token: String, identifier: String, idProposal: Int) {
        _listBimbinganProposalMahasiswa.postValue(Resource.Loading())
        try {
            if (hasInternectConnection()) {
                val response = sitamRepository.listBimbinganProposalDosen(token, identifier, idProposal)
                _listBimbinganProposalMahasiswa.postValue(handleBimbinganProposalResponse(response))
            } else {
                _listBimbinganProposalMahasiswa.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _listBimbinganProposalMahasiswa.postValue(Resource.Error("Network Failure"))
                else -> _listBimbinganProposalMahasiswa.postValue(Resource.Error("Conversion Error"))
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