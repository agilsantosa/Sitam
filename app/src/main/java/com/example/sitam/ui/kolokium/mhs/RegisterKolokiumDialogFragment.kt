package com.example.sitam.ui.kolokium.mhs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.sitam.R
import com.example.sitam.ui.kolokium.mhs.viewmodel.KolokiumMhsViewModel
import com.example.sitam.ui.ta.mhs.TugasAkhirMahasiswaFragment
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.register_kolokium_mhs_dialog_fragment.*

class RegisterKolokiumDialogFragment: DialogFragment() {
    private val kolokiumMhsViewModel : KolokiumMhsViewModel by activityViewModels()
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private var onRegisterKolokiumDialogListener: OnRegisterKolokiumDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_kolokium_mhs_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        val idProposal = preferenceProvider.getIdProposal(Constants.KEY_ID_PROPOSAL).toString()
        val identifier = preferenceProvider.getIdentifierUser(Constants.KEY_IDENTIFIE_USER).toString()

        register_kolokium_confirmation_cancel_button.setOnClickListener {
            dialog?.dismiss()
        }
        
        register_kolokium_confirmation_upload_button.setOnClickListener {
            kolokiumMhsViewModel.getDaftarKolokium(token, identifier, idProposal)
        }

        kolokiumMhsViewModel.requestDaftarKolokium.observe(viewLifecycleOwner,  { response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    response.data?.let {
                        if (onRegisterKolokiumDialogListener != null){
                            onRegisterKolokiumDialogListener?.onMessageRegister(it.message)
                        }
                        dialog?.dismiss()
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        if (onRegisterKolokiumDialogListener != null){
                            onRegisterKolokiumDialogListener?.onMessageRegister(message)
                        }
                        dialog?.dismiss()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    interface OnRegisterKolokiumDialogListener{
        fun onMessageRegister(text: String)
    }

    private fun showProgressBar() {
        register_kolokium_confirmation_progress_bar.visibility = View.VISIBLE
        register_kolokium_confirmation_upload_button.visibility = View.GONE
    }

    private fun hideProgressBar() {
        register_kolokium_confirmation_progress_bar.visibility = View.GONE
        register_kolokium_confirmation_upload_button.visibility = View.VISIBLE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val fragment = parentFragment

        if (fragment is TugasAkhirMahasiswaFragment){
            val tugasAkhirMahasiswaFragment = fragment
            this.onRegisterKolokiumDialogListener = tugasAkhirMahasiswaFragment.onRegiterKolokiumDialog
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.onRegisterKolokiumDialogListener = null
    }
}