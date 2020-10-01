package com.example.sitam.ui.proposal.mhs.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sitam.ui.proposal.dosen.viewmodel.ProposalTabViewModel

class ProposalViewModelFactory(
    private val app: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DetailProposalViewModel::class.java) -> {
                DetailProposalViewModel(app) as T
            }
            modelClass.isAssignableFrom(ProposalViewModel::class.java) -> {
                ProposalViewModel(app) as T
            }
            modelClass.isAssignableFrom(ProposalTabViewModel::class.java) -> {
                ProposalTabViewModel(app) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }
}