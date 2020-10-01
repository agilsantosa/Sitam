package com.example.sitam.ui.kolokium.dosen

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
import com.example.sitam.databinding.FragmentKolokiumTabDosenBinding
import com.example.sitam.ui.kolokium.dosen.adapter.BimbinganKolokiumViewDosenAdapter
import com.example.sitam.ui.kolokium.dosen.viewmodel.KolokiumTabDosenViewModel
import com.example.sitam.ui.seminar.dosen.adapter.BimbinganSeminarViewDosenAdapter
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider

class KolokiumTabDosenFragment : Fragment() {

    private var _binding: FragmentKolokiumTabDosenBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private val kolokiumTabDosenViewModel: KolokiumTabDosenViewModel by activityViewModels()
    private lateinit var bimbinganKolokiumViewDosenAdapter: BimbinganKolokiumViewDosenAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_kolokium_tab_dosen, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceProvider = SharedPreferenceProvider(requireContext().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        val identifier =
            preferenceProvider.getIdentifierUser(Constants.KEY_IDENTIFIE_USER).toString()

        setupRecyclerView()

        kolokiumTabDosenViewModel.getBimbinganSeminarDosen(token, identifier)
        kolokiumTabDosenViewModel.requestBimbinganKolokiumDosen.observe(viewLifecycleOwner, { response ->
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
                            bimbinganKolokiumViewDosenAdapter.differ.submitList(list)
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

        bimbinganKolokiumViewDosenAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("dataBimbinganKolokium", it)
                if (it.id != null){
                    preferenceProvider.saveIdKolokium(Constants.KEY_ID_KOLOKIUM, it.id.toString())
                    preferenceProvider.saveAlias(Constants.KEY_ALIAS, it.`as`)
                }
            }
            findNavController().navigate(R.id.action_tugasAkhirDosenFragment_to_listBimbinganKolokiumViewDosenFragment, bundle)
        }
    }

    private fun setupRecyclerView() {
        bimbinganKolokiumViewDosenAdapter = BimbinganKolokiumViewDosenAdapter()
        binding.rvKolkiumViewDosen.apply {
            adapter = bimbinganKolokiumViewDosenAdapter
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvKolkiumViewDosen.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvKolkiumViewDosen.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }
}