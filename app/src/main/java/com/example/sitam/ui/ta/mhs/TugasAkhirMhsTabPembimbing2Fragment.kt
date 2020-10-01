package com.example.sitam.ui.ta.mhs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.sitam.R
import com.example.sitam.databinding.FragmentTugasAkhirMhsTabPembimbing2Binding
import com.example.sitam.ui.ta.mhs.adapter.ListBimbinganTaViewMhsAdapter
import com.example.sitam.ui.ta.mhs.viewmodel.TugasAkhirMahasiswaViewModel
import com.example.sitam.ui.ta.mhs.viewmodel.TugasAkhirTabPembimbing2ViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider

class TugasAkhirMhsTabPembimbing2Fragment : Fragment() {

    private var _binding: FragmentTugasAkhirMhsTabPembimbing2Binding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private lateinit var token: String
    private lateinit var idTa: String
    private lateinit var listBimbinganTaViewMhsAdapter: ListBimbinganTaViewMhsAdapter
    private val tugasAkhirMahasiswaViewModel: TugasAkhirMahasiswaViewModel by activityViewModels()
    private val tugasAkhirTabPembimbing1ViewModel: TugasAkhirTabPembimbing2ViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_tugas_akhir_mhs_tab_pembimbing2, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        idTa = preferenceProvider.getIdTa(Constants.KEY_ID_TA).toString()

        setupRecylerView()
        subscribe()

        tugasAkhirTabPembimbing1ViewModel.getListbBimbinganSeminar(token, idTa, "Pembimbing 2")
        tugasAkhirTabPembimbing1ViewModel.requestListBimbinganTa.observe(viewLifecycleOwner,{ response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    response.data?.let {
                        val data = it.data
                        if (data.isNullOrEmpty()) {
                            binding.emptyText.visibility = View.VISIBLE
                        } else {
                            binding.emptyText.visibility = View.GONE
                            val list = data.sortedByDescending { it.id }
                            listBimbinganTaViewMhsAdapter.differ.submitList(list)
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

        listBimbinganTaViewMhsAdapter.setOnClickListener {data->
            Intent(context, DetailBimbinganTaMhsActivity::class.java).also {
                it.putExtra("ID_TA", data)
                startActivity(it)
            }
        }
    }

    private fun subscribe() {
        val statusListener = Observer<String?> { message ->
            if (message == "Bimbingan Ta create successfully!") {
                tugasAkhirTabPembimbing1ViewModel.getListbBimbinganSeminar(token, idTa, "Pembimbing 2")
            }
        }
        tugasAkhirMahasiswaViewModel.getStatus().observe(viewLifecycleOwner, statusListener)
    }

    private fun setupRecylerView() {
        listBimbinganTaViewMhsAdapter = ListBimbinganTaViewMhsAdapter()
        binding.rvTugasAkhirPembimbing2Mhs.apply {
            smoothScrollToPosition(0)
            adapter = listBimbinganTaViewMhsAdapter
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvTugasAkhirPembimbing2Mhs.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvTugasAkhirPembimbing2Mhs.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }

}