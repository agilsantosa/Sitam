package com.example.sitam.ui.ta.dosen

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
import com.example.sitam.databinding.FragmentTugasAkhirTabDosenBinding
import com.example.sitam.ui.ta.dosen.adapter.ListTugasAkhirMahasiswaAdapter
import com.example.sitam.ui.ta.dosen.viewmodel.TugasAkhirTabDosenViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider

class TugasAkhirTabDosenFragment : Fragment() {

    private var _binding: FragmentTugasAkhirTabDosenBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private lateinit var listTugasAkhirMahasiswaAdapter: ListTugasAkhirMahasiswaAdapter
    private val tugasAkhirTabDosenViewModel: TugasAkhirTabDosenViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_tugas_akhir_tab_dosen, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        val identifier = preferenceProvider.getIdentifierUser(Constants.KEY_IDENTIFIE_USER).toString()

        setupRecyclerView()

        tugasAkhirTabDosenViewModel.getTaMahasiswa(token, identifier)
        tugasAkhirTabDosenViewModel.listTaMahasiswa.observe(viewLifecycleOwner, { response ->
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
                            listTugasAkhirMahasiswaAdapter.differ.submitList(list)
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

        listTugasAkhirMahasiswaAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("dataTugasAkhirDosen", it)
            }
            if (it.id != null){
                preferenceProvider.saveIdTa(Constants.KEY_ID_TA, it.id.toString())
                preferenceProvider.saveAlias(Constants.KEY_ALIAS, it.`as`)
            }
            findNavController().navigate(R.id.action_tugasAkhirDosenFragment_to_listBimbinganTaDosenFragment, bundle)
        }
    }

    private fun setupRecyclerView() {
        listTugasAkhirMahasiswaAdapter = ListTugasAkhirMahasiswaAdapter()
        binding.rvTugasAkhirViewDosen.apply {
            adapter = listTugasAkhirMahasiswaAdapter
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvTugasAkhirViewDosen.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvTugasAkhirViewDosen.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }
}