package com.example.sitam.ui.seminar.dosen

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sitam.R
import com.example.sitam.databinding.FragmentListBimbinganSeminarViewDosenBinding
import com.example.sitam.ui.proposal.dosen.adapter.ListBimbinganProposalMhsAdapter
import com.example.sitam.ui.seminar.dosen.adapter.ListBimbinganSeminarViewDosenAdapter
import com.example.sitam.ui.seminar.dosen.viewmodel.SeminarTabDosenViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.fragment_list_bimbingan_proposal_dosen.*

class ListBimbinganSeminarViewDosenFragment : Fragment() {
    private var _binding: FragmentListBimbinganSeminarViewDosenBinding? = null
    private val binding get() = _binding!!
//    private val args: ListBimbinganSeminarViewDosenFragmentArgs by navArgs()
    private lateinit var listBimbinganSeminarViewDosenAdapter: ListBimbinganSeminarViewDosenAdapter
    private val seminarTabDosenViewModel: SeminarTabDosenViewModel by viewModels()
    private lateinit var preferenceProvider: SharedPreferenceProvider


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list_bimbingan_seminar_view_dosen,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        val id = preferenceProvider.getIdSeminar(Constants.KEY_ID_SEMINAR).toString()
        val alias = preferenceProvider.getAlias(Constants.KEY_ALIAS).toString()

        seminarTabDosenViewModel.getListBimbinganSeminarDosen(token, id, alias)
        seminarTabDosenViewModel.requestListBimbinganSeminarDosen.observe(
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
                                listBimbinganSeminarViewDosenAdapter.differ.submitList(data.sortedByDescending { it.id })
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
            findNavController().navigate(R.id.action_listBimbinganSeminarViewDosenFragment_to_proposalDosenFragment)
        }

        listBimbinganSeminarViewDosenAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("datalistBimbingan", it)
            }
            findNavController().navigate(R.id.action_listBimbinganSeminarViewDosenFragment_to_detailBimbinganSeminarViewDosenFragment, bundle)
        }

    }

    private fun setupRecyclerView() {
        listBimbinganSeminarViewDosenAdapter = ListBimbinganSeminarViewDosenAdapter()
        binding.rvBimbinganSeminardosenview.apply {
            adapter = listBimbinganSeminarViewDosenAdapter
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


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.filter_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        setMode(item.itemId)
//        return super.onOptionsItemSelected(item)
//    }
//
//    private fun setMode(selectedMode: Int) {
//        when (selectedMode) {
//            R.id.action_approved -> {
////                status = "Disetujui"
//            }
//            R.id.action_revisi -> {
////                status = "Belum ditanggapi"
//            }
//        }
//    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvBimbinganSeminardosenview.visibility = View.VISIBLE
        binding.shimmerLayoutListBimbinganSeminar.visibility = View.GONE
        binding.shimmerLayoutListBimbinganSeminar.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvBimbinganSeminardosenview.visibility = View.GONE
        binding.shimmerLayoutListBimbinganSeminar.visibility = View.VISIBLE
        binding.shimmerLayoutListBimbinganSeminar.startShimmer()
    }
}