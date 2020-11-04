package com.example.sitam.ui.proposal.mhs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sitam.R
import com.example.sitam.databinding.FragmentEditProposalBinding
import com.example.sitam.ui.proposal.mhs.viewmodel.EditProposalViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.fragment_list_bimbingan_proposal_dosen.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class EditProposalFragment : Fragment() {

    private var _binding: FragmentEditProposalBinding? = null
    private val binding get() = _binding!!
    private val editProposalViewModel: EditProposalViewModel by viewModels()
    private val args: EditProposalFragmentArgs by navArgs()
    private lateinit var preference: SharedPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_proposal, container, false)

        preference = SharedPreferenceProvider(requireActivity().applicationContext)
        binding.editProposalTitle.setText(arguments?.getString("judulProposal"))
        setSpinText(binding.editProposalKonsentrasi, arguments?.getString("konsentrasi"))
        binding.editProposalTopik.setText(arguments?.getString("topik"))
        return binding.root
    }

    fun setSpinText(spin: Spinner, text: String?) {
        for (i in 0 until spin.adapter.count) {
            if (spin.adapter.getItem(i).toString().equals(text!!)) {
                spin.setSelection(i)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeView()
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_editProposalFragment_to_proposalMahasiswaFragment2)
        }

        binding.editProposalAddButton.setOnClickListener {
            val token = preference.getTokenUser(Constants.KEY_TOKEN_USER)
            val identifier = preference.getIdentifierUser(Constants.KEY_IDENTIFIE_USER)
            val idProposal = preference.getIdProposal(Constants.KEY_ID_PROPOSAL)!!

            val konsentrasi = binding.editProposalKonsentrasi.selectedItem.toString()
            val judulProposal = binding.editProposalTitle.text.toString()
            val topik = binding.editProposalTopik.text.toString()

            if (token!!.isNotEmpty() and identifier!!.isNotEmpty() and konsentrasi.isNotEmpty() and judulProposal.isNotEmpty() and topik.isNotEmpty()) {
                editProposalViewModel.editProposal(
                    token,
                    idProposal,
                    identifier,
                    judulProposal,
                    konsentrasi,
                    topik
                )
//                GlobalScope.launch {
//                    delay(1500)
//                    findNavController().navigate(R.id.action_editProposalFragment_to_proposalMahasiswaFragment2)
//                }
            } else {
                Toast.makeText(activity, "Field Cannot Empty", Toast.LENGTH_SHORT).show()
//            }
            }
        }

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
        }.apply {
            supportActionBar?.apply {
                setDisplayShowHomeEnabled(true)
                setDisplayShowTitleEnabled(false)
            }
        }
    }


    private fun observeView() {
        editProposalViewModel.editProposal.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Succes -> {
                    binding.editSessionProgressBar.visibility = View.GONE
                    response.data?.let {
                        showToast(it.message)
                    }
                }
                is Resource.Error -> {
                    binding.editSessionProgressBar.visibility = View.GONE
                    response.message?.let { message ->
                        showToast(message)
                    }
                }
                is Resource.Loading -> {
                    binding.editSessionProgressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

