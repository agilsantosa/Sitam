package com.example.sitam.ui.proposal.dosen

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sitam.R
import com.example.sitam.databinding.FragmentListBimbinganProposalDosenBinding
import com.example.sitam.ui.chat.adapter.PesanMhsAdapter
import com.example.sitam.ui.proposal.dosen.adapter.ListBimbinganProposalMhsAdapter
import com.example.sitam.ui.proposal.dosen.adapter.ListProposalMahasiswaAdapter
import com.example.sitam.ui.proposal.dosen.viewmodel.ProposalTabViewModel
import com.example.sitam.ui.proposal.mhs.viewmodel.ProposalViewModelFactory
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.activity_detail_bimbingan_proposal_mhs.*
import kotlinx.android.synthetic.main.fragment_home_mahasiswa.*
import kotlinx.android.synthetic.main.fragment_list_bimbingan_proposal_dosen.*
import kotlinx.android.synthetic.main.fragment_list_bimbingan_proposal_dosen.toolbar

class ListBimbinganProposalDosenFragment : Fragment() {
    private lateinit var binding: FragmentListBimbinganProposalDosenBinding
    private lateinit var listBimbinganproposalMhsAdapter: ListBimbinganProposalMhsAdapter
    private lateinit var proposalTabViewModel: ProposalTabViewModel
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private val args: ListBimbinganProposalDosenFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list_bimbingan_proposal_dosen,
            container,
            false
        )
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val proposalViewModelFactory =
            ProposalViewModelFactory(requireActivity().application)
        proposalTabViewModel =
            ViewModelProvider(this, proposalViewModelFactory)[ProposalTabViewModel::class.java]
        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)

        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        val identifier = preferenceProvider.getIdentifierUser(Constants.KEY_IDENTIFIE_USER).toString()
        val idProposal = preferenceProvider.getIdProposal(Constants.KEY_ID_PROPOSAL)
//        val idProposal = args.idProposal

        proposalTabViewModel.getListBimbinganProposal(token, identifier, idProposal!!.toInt())
        proposalTabViewModel.listBimbinganProposalMahasiswa.observe(
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
//                                if (status != null) {
//                                    listBimbinganproposalMhsAdapter.differ.submitList(data.filter { it.status.equals(status)})
//                                } else {
                                    listBimbinganproposalMhsAdapter.differ.submitList(data.sortedByDescending { it.id })
//                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            showToast(message)
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            })

        setupRecyclerView()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(
                R.id.action_listBimbinganProposalDosenFragment_to_proposalDosenFragment
            )
        }

        listBimbinganproposalMhsAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("proposalFile", it)
            }
            if (it.status.equals("Disetujui")){
                Toast.makeText(context, "Proposal sudah disetujui", Toast.LENGTH_SHORT).show()
            }else{
                findNavController().navigate(
                    R.id.action_listBimbinganProposalDosenFragment_to_detailBimbinganProposalDosenFragment,
                    bundle
                )
            }
        }

    }

    private fun setupRecyclerView() {
        listBimbinganproposalMhsAdapter = ListBimbinganProposalMhsAdapter()
        binding.rvBimbinganProposaldosenview.apply {
            adapter = listBimbinganproposalMhsAdapter
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMode(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setMode(selectedMode: Int) {
        when (selectedMode) {
            R.id.action_approved -> {
//                status = "Disetujui"
            }
            R.id.action_revisi -> {
//                status = "Belum ditanggapi"
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvBimbinganProposaldosenview.visibility = View.VISIBLE
        binding.shimmerLayoutListBimbinganProposal.visibility = View.GONE
        binding.shimmerLayoutListBimbinganProposal.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvBimbinganProposaldosenview.visibility = View.GONE
        binding.shimmerLayoutListBimbinganProposal.visibility = View.VISIBLE
        binding.shimmerLayoutListBimbinganProposal.startShimmer()
    }
}