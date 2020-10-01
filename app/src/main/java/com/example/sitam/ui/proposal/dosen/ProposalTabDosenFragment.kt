package com.example.sitam.ui.proposal.dosen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sitam.R
import com.example.sitam.databinding.FragmentProposalTabDosenBinding
import com.example.sitam.ui.proposal.dosen.adapter.ListProposalMahasiswaAdapter
import com.example.sitam.ui.proposal.dosen.viewmodel.ProposalTabViewModel
import com.example.sitam.ui.proposal.mhs.viewmodel.ProposalViewModelFactory
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider

class ProposalTabDosenFragment : Fragment() {

    private var _binding: FragmentProposalTabDosenBinding? = null
    private val binding get() = _binding!!
    private val proposalTabViewModel: ProposalTabViewModel by activityViewModels()
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private lateinit var listProposalMahasiswaAdapter: ListProposalMahasiswaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_proposal_tab_dosen,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)

        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        val identifier =
            preferenceProvider.getIdentifierUser(Constants.KEY_IDENTIFIE_USER).toString()
        val level = preferenceProvider.getLevelUser(Constants.KEY_LEVEL_USER).toString()

        setupRecyclerView()

        proposalTabViewModel.getProposalMahasiswa(token, identifier, level)
        proposalTabViewModel.listProposalMahasiswa.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.Succes -> {
                        hideProgressBar()
                        response.data?.let { it ->
                            val data = it.data
                            if (data.isNullOrEmpty()) {
                                binding.emptyText.visibility = View.VISIBLE
                            } else {
                                binding.emptyText.visibility = View.GONE
                                var list = data.sortedByDescending { it.id }
                                listProposalMahasiswaAdapter.differ.submitList(list)
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            when (message) {
                                "Not Found" -> binding.emptyText.visibility = View.VISIBLE
                                else -> showToast(message)
                            }
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            })


        listProposalMahasiswaAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("idProposal", it.id)
                if (it.id != null){
                    preferenceProvider.saveIdProposal(Constants.KEY_ID_PROPOSAL, it.id.toString())
                }
            }
            findNavController().navigate(R.id.action_proposalDosenFragment_to_listBimbinganProposalDosenFragment, bundle)
        }
    }

    private fun setupRecyclerView() {
        listProposalMahasiswaAdapter = ListProposalMahasiswaAdapter()
        binding.rvProposalViewDosen.apply {
            adapter = listProposalMahasiswaAdapter
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvProposalViewDosen.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvProposalViewDosen.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }
}