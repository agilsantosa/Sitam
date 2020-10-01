package com.example.sitam.ui.proposal.mhs.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sitam.repository.SitamRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddProposalViewModel(app: Application) : AndroidViewModel(app) {

    private val sitamRepository = SitamRepository()

    val addNewProposalDataLoadState: LiveData<DataLoadState>
        get() = _addNewProposalDataLoadState
    private val _addNewProposalDataLoadState =
        MutableLiveData<DataLoadState>(DataLoadState.Unloaded)

    fun addNewProposal(
        dispatcher: CoroutineDispatcher = Dispatchers.IO, token: String,
        identifier: String,
        judulProposal: String,
        konsentrasi: String,
        topik: String
    ) {
        viewModelScope.launch {
            requestAddProposal(token, identifier, judulProposal, konsentrasi, topik)
        }
    }

    private suspend fun requestAddProposal(
        token: String,
        identifier: String,
        judulProposal: String,
        konsentrasi: String,
        topik: String
    ) {
        _addNewProposalDataLoadState.postValue(DataLoadState.Loading)
        try {
            val respon =
                sitamRepository.addNewProposal(token, identifier, judulProposal, konsentrasi, topik)
            if (respon.isSuccessful) {
                val data = respon.body()!!
                _addNewProposalDataLoadState.postValue(DataLoadState.Loaded(data))
            } else {
                _addNewProposalDataLoadState.postValue(DataLoadState.Error("Gagal Memuat Data, Silahkan Periksa Koneksi Internet"))
            }
        } catch (exception: Exception) {
            _addNewProposalDataLoadState.postValue(
                DataLoadState.Error(
                    exception.message ?: "Gagal Memuat Data, Silahkan Periksa Koneksi Internet"
                )
            )
        }
    }

    companion object {
        sealed class DataLoadState {
            class Loaded(val data: Any) : DataLoadState()
            object Unloaded : DataLoadState()
            object Loading : DataLoadState()
            class Error(val errorMessage: String) : DataLoadState()
        }
    }
}