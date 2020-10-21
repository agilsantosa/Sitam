package com.example.sitam.ui.proposal.mhs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.sitam.R
import com.example.sitam.databinding.FragmentAddProposalBinding
import com.example.sitam.ui.proposal.mhs.viewmodel.AddNewProposalViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.fragment_list_bimbingan_proposal_dosen.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AddProposalFragment : Fragment() {

    private var _binding: FragmentAddProposalBinding? = null
    private val binding get() = _binding!!
    private val addNewProposalViewModel: AddNewProposalViewModel by activityViewModels()

    private lateinit var preference: SharedPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_proposal, container, false)

        preference = SharedPreferenceProvider(requireActivity().applicationContext)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeView()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_addProposalFragment_to_proposalMahasiswaFragment2)
        }

        binding.addNewProposalAddButton.setOnClickListener {
            val token = preference.getTokenUser(Constants.KEY_TOKEN_USER)
            val identifier = preference.getIdentifierUser(Constants.KEY_IDENTIFIE_USER)
            val konsentrasi = binding.addNewProposalKonsentrasi.selectedItem.toString()
            val judulProposal = binding.addNewProposalTitle.text.toString()
            val topik = binding.addNewProposalTopik.text.toString()

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
                    findNavController().navigate(R.id.action_addProposalFragment_to_proposalMahasiswaFragment2)
                }
            } else {
                Toast.makeText(activity, "Field Cannot Empty", Toast.LENGTH_SHORT).show()
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
        addNewProposalViewModel.addProposal.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Succes -> {
                    binding.addNewSessionProgressBar.visibility = View.GONE
                    response.data?.let {
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    binding.addNewSessionProgressBar.visibility = View.GONE
                    response.message?.let { message ->
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    binding.addNewSessionProgressBar.visibility = View.VISIBLE
                }
            }
        })
    }

}