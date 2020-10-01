package com.example.sitam.ui.ta.dosen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sitam.R
import com.example.sitam.databinding.FragmentListBimbinganTaDosenBinding
import com.example.sitam.ui.proposal.dosen.adapter.ListBimbinganProposalMhsAdapter
import com.example.sitam.ui.ta.dosen.adapter.ListBimbinganTugasAkhirDosenAdapter
import com.example.sitam.ui.ta.dosen.viewmodel.TugasAkhirTabDosenViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.fragment_list_bimbingan_proposal_dosen.*

class ListBimbinganTaDosenFragment : Fragment() {

    private val args: ListBimbinganTaDosenFragmentArgs by navArgs()
    private var _binding: FragmentListBimbinganTaDosenBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private lateinit var listBimbinganTugasAkhirDosenAdapter: ListBimbinganTugasAkhirDosenAdapter
    private val tugasAkhirTabDosenViewModel: TugasAkhirTabDosenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list_bimbingan_ta_dosen,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        super.onViewCreated(view, savedInstanceState)

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)

        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        val idTa = preferenceProvider.getIdTa(Constants.KEY_ID_TA).toString()
        val alias = preferenceProvider.getIdTa(Constants.KEY_ALIAS).toString()
        val data = args.dataTugasAkhirDosen

        tugasAkhirTabDosenViewModel.getBimbinganTaMahasiswa(token, idTa, alias)
        tugasAkhirTabDosenViewModel.listBimbinganTaMahasiswa.observe(
            viewLifecycleOwner,
            { response ->
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
                                listBimbinganTugasAkhirDosenAdapter.differ.submitList(data.sortedByDescending { it.id })
//                                }
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

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_listBimbinganTaDosenFragment_to_tugasAkhirDosenFragment)
        }

        listBimbinganTugasAkhirDosenAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("dataListBimbingan", it)
            }
            findNavController().navigate(
                R.id.action_listBimbinganTaDosenFragment_to_detailBimbinganTaDosenFragment,
                bundle
            )
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

    private fun setupRecyclerView() {
        listBimbinganTugasAkhirDosenAdapter = ListBimbinganTugasAkhirDosenAdapter()
        binding.rvBimbinganTadosenview.apply {
            adapter = listBimbinganTugasAkhirDosenAdapter
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvBimbinganTadosenview.visibility = View.VISIBLE
        binding.shimmerLayoutListBimbinganTa.visibility = View.GONE
        binding.shimmerLayoutListBimbinganTa.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvBimbinganTadosenview.visibility = View.GONE
        binding.shimmerLayoutListBimbinganTa.visibility = View.VISIBLE
        binding.shimmerLayoutListBimbinganTa.startShimmer()
    }
}