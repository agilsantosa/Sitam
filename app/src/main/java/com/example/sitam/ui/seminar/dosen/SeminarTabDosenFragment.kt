package com.example.sitam.ui.seminar.dosen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sitam.R
import com.example.sitam.databinding.FragmentSeminarTabDosenBinding
import com.example.sitam.ui.seminar.dosen.adapter.BimbinganSeminarViewDosenAdapter
import com.example.sitam.ui.seminar.dosen.viewmodel.SeminarTabDosenViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider

class SeminarTabDosenFragment : Fragment() {

    private val seminarTabDosenViewModel: SeminarTabDosenViewModel by activityViewModels()
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private var _binding: FragmentSeminarTabDosenBinding? = null
    private val binding get() = _binding!!
    private lateinit var bimbinganSeminarViewDosenAdapter: BimbinganSeminarViewDosenAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_seminar_tab_dosen, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceProvider = SharedPreferenceProvider(requireContext().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        val identifier =
            preferenceProvider.getIdentifierUser(Constants.KEY_IDENTIFIE_USER).toString()

        setupRecyclerView()

        seminarTabDosenViewModel.getBimbinganSeminarDosen(token, identifier)
        seminarTabDosenViewModel.requestBimbinganSeminarDosen.observe(
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
                                val list = data.sortedByDescending { it.id }
                                bimbinganSeminarViewDosenAdapter.differ.submitList(list)
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

        bimbinganSeminarViewDosenAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("dataBimbinganSeminar", it)
                if (it.id != null){
                    preferenceProvider.saveIdSeminar(Constants.KEY_ID_SEMINAR, it.id.toString())
                    preferenceProvider.saveAlias(Constants.KEY_ALIAS, it.`as`)
                }
            }
            findNavController().navigate(R.id.action_proposalDosenFragment_to_listBimbinganSeminarViewDosenFragment, bundle)
        }
    }

    private fun setupRecyclerView() {
        bimbinganSeminarViewDosenAdapter = BimbinganSeminarViewDosenAdapter()
        binding.rvSeminarViewDosen.apply {
            adapter = bimbinganSeminarViewDosenAdapter
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvSeminarViewDosen.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvSeminarViewDosen.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }
}