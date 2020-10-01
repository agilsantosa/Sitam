package com.example.sitam.ui.seminar.mhs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.sitam.R
import com.example.sitam.ui.proposal.mhs.ProposalMahasiswaFragment
import com.example.sitam.ui.seminar.mhs.viewmodel.SeminarMhsViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.register_seminar_mahasiswa.*

class RegisterSeminarMhsDialogFragment: DialogFragment() {

    private val seminarMahasiViewModel: SeminarMhsViewModel by activityViewModels()
    private var onRegisterSeminarDialogListener: OnRegisterSeminarDialogListener? = null
    private lateinit var preferenceProvider: SharedPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_seminar_mahasiswa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        val idProposal = preferenceProvider.getIdProposal(Constants.KEY_ID_PROPOSAL).toString()
        val identifier = preferenceProvider.getIdentifierUser(Constants.KEY_IDENTIFIE_USER).toString()



        register_seminar_confirmation_cancel_button.setOnClickListener {
            dialog?.dismiss()
        }

        register_seminar_confirmation_upload_button.setOnClickListener {
           seminarMahasiViewModel.getDaftarSeminar(token, identifier, idProposal)
        }

        seminarMahasiViewModel.requestDaftarSeminar.observe(viewLifecycleOwner,  { response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    response.data?.let {

                        if (onRegisterSeminarDialogListener != null){
                            onRegisterSeminarDialogListener?.onMessageRegister(it.message)
                        }
                        dialog?.dismiss()
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        if (onRegisterSeminarDialogListener != null){
                            onRegisterSeminarDialogListener?.onMessageRegister(message)
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

    private fun showProgressBar() {
        register_seminar_confirmation_progress_bar.visibility = View.VISIBLE
        register_seminar_confirmation_upload_button.visibility = View.GONE
    }

    private fun hideProgressBar() {
        register_seminar_confirmation_progress_bar.visibility = View.GONE
        register_seminar_confirmation_upload_button.visibility = View.VISIBLE
    }

    interface OnRegisterSeminarDialogListener{
        fun onMessageRegister(text: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val fragment = parentFragment

        if (fragment is ProposalMahasiswaFragment){
            val proposalMahasiswaFragment = fragment
            this.onRegisterSeminarDialogListener = proposalMahasiswaFragment.onRegiterSeminarDialog
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.onRegisterSeminarDialogListener = null
    }
}