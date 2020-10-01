package com.example.sitam.ui.proposal.mhs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sitam.R
import com.example.sitam.ui.proposal.mhs.viewmodel.AddNewProposalViewModel
import com.example.sitam.ui.proposal.mhs.viewmodel.ProposalViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.add_new_proposal_dialog_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Suppress("UNREACHABLE_CODE")
class AddNewProposalDialogFragment : DialogFragment() {

    private lateinit var addNewProposalViewModel: AddNewProposalViewModel
    private lateinit var proposalViewModel: ProposalViewModel

    companion object {
        const val EXTRA_SELECTED_VALUE = "extra_selected_value"
        const val RESULT_CODE = 110
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_new_proposal_dialog_fragment, container, false)
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNewProposalViewModel = ViewModelProvider(this)[AddNewProposalViewModel::class.java]
        proposalViewModel = ViewModelProvider(this)[ProposalViewModel::class.java]
        val preference = SharedPreferenceProvider(requireActivity().applicationContext)

        add_new_proposal_add_button.setOnClickListener {
            val token = preference.getTokenUser(Constants.KEY_TOKEN_USER)
            val identifier = preference.getIdentifierUser(Constants.KEY_IDENTIFIE_USER)
            val konsentrasi = add_new_proposal_konsentrasi.selectedItem.toString()
            val judulProposal = add_new_proposal_title.text.toString()
            val topik = add_new_proposal_topik.text.toString()

            if (token!!.isNotEmpty() and identifier!!.isNotEmpty() and konsentrasi.isNotEmpty() and judulProposal.isNotEmpty() and topik.isNotEmpty()) {
                addNewProposalViewModel.addNewProposal(
                    token,
                    identifier,
                    konsentrasi,
                    judulProposal,
                    topik
                )
                GlobalScope.launch {
                    delay(1500)
                    dialog?.dismiss()
                }
            } else {
                Toast.makeText(activity, "Field Cannot Empty", Toast.LENGTH_SHORT).show()
            }
        }

        add_new_session_cancel_button.setOnClickListener {
            dialog?.dismiss()
        }

        addNewProposalViewModel.addProposal.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Succes -> {
                    add_new_session_progress_bar.visibility = View.GONE
                    response.data?.let {
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    add_new_session_progress_bar.visibility = View.GONE
                    response.message?.let { message ->
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    add_new_session_progress_bar.visibility = View.VISIBLE
                }
            }
        })
    }


}