package com.example.sitam.ui.seminar.mhs

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
import androidx.navigation.fragment.findNavController
import com.example.sitam.MainActivity
import com.example.sitam.R
import com.example.sitam.databinding.FragmentSeminarTabPembimbingBinding
import com.example.sitam.ui.seminar.mhs.adapter.ListBimbinganSeminarViewMhsAdapter
import com.example.sitam.ui.seminar.mhs.viewmodel.SeminarMhsViewModel
import com.example.sitam.ui.seminar.mhs.viewmodel.SeminarTabPembimbingViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider

class SeminarTabPembimbingFragment : Fragment() {

    private var _binding: FragmentSeminarTabPembimbingBinding? = null
    private val binding get() = _binding!!
    private val seminarTabPembimbingViewModel: SeminarTabPembimbingViewModel by activityViewModels()
    private val seminarMhsViewModel: SeminarMhsViewModel by activityViewModels()
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private lateinit var listBimbinganSeminarAdapter: ListBimbinganSeminarViewMhsAdapter
    private lateinit var token: String
    private lateinit var idSeminar: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_seminar_tab_pembimbing,
            container,
            false
        )

        setupRecylerView()

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        idSeminar = preferenceProvider.getIdProposal(Constants.KEY_ID_SEMINAR).toString()

        seminarTabPembimbingViewModel.getListbBimbinganSeminar(token, idSeminar, "Pembimbing")
        seminarTabPembimbingViewModel.requestListBimbinganSeminar.observe(viewLifecycleOwner, { response ->
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
                            listBimbinganSeminarAdapter.differ.submitList(list)
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

        listBimbinganSeminarAdapter.setOnClickListener { data->
           Intent(context, DetailBimbinganSeminarMhsActivity::class.java).also {
               it.putExtra("ID_SEMINAR", data)
               startActivity(it)
           }
        }

        return binding.root
    }

    private fun setupRecylerView() {
        listBimbinganSeminarAdapter = ListBimbinganSeminarViewMhsAdapter()
        binding.rvSeminarPembimbingMhs.apply {
            smoothScrollToPosition(0)
            adapter = listBimbinganSeminarAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribe()
    }

    private fun subscribe() {
        val statusListener = Observer<String?> { message ->
            if (message == "Revisi Seminar create successfully!") {
                seminarTabPembimbingViewModel.getListbBimbinganSeminar(token, idSeminar, "Pembimbing")
            }
        }
        seminarMhsViewModel.getStatus().observe(viewLifecycleOwner, statusListener)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvSeminarPembimbingMhs.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvSeminarPembimbingMhs.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }

}