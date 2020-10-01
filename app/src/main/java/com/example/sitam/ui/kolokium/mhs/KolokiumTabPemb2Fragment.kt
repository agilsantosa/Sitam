package com.example.sitam.ui.kolokium.mhs

import android.content.Intent
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
import com.example.sitam.R
import com.example.sitam.databinding.FragmentKolokiumTabPemb2Binding
import com.example.sitam.ui.kolokium.mhs.adaper.ListBimbinganKolokiumViewMhsAdapter
import com.example.sitam.ui.kolokium.mhs.viewmodel.KolokiumMhsViewModel
import com.example.sitam.ui.kolokium.mhs.viewmodel.KolokiumTabPemb2ViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider


class KolokiumTabPemb2Fragment : Fragment() {

    private var _binding: FragmentKolokiumTabPemb2Binding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private lateinit var listBimbinganKolokiumViewMhsAdapter: ListBimbinganKolokiumViewMhsAdapter
    private val kolokiumMhsViewModel: KolokiumMhsViewModel by activityViewModels()
    private val kolokiumTabPemb2ViewModel: KolokiumTabPemb2ViewModel by activityViewModels()
    private lateinit var token: String
    private lateinit var idKolokium: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kolokium_tab_pemb2, container, false)

        setupRecylerView()

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        idKolokium = preferenceProvider.getIdKolokium(Constants.KEY_ID_KOLOKIUM).toString()

        kolokiumTabPemb2ViewModel.getListbBimbinganKolokium(token, idKolokium, "Pembimbing 2")
        kolokiumTabPemb2ViewModel.requestListBimbinganKolokium.observe(viewLifecycleOwner, { response ->
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
                            listBimbinganKolokiumViewMhsAdapter.differ.submitList(list)
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

        listBimbinganKolokiumViewMhsAdapter.setOnClickListener { data ->
            Intent(context, DetailBimbinganKolokiumMhsActivity::class.java).also {
                it.putExtra("ID_KOLOKIUM", data)
                startActivity(it)
            }
        }

        return binding.root
    }

    private fun setupRecylerView() {
        listBimbinganKolokiumViewMhsAdapter = ListBimbinganKolokiumViewMhsAdapter()
        binding.rvKolokiumPembimbing2Mhs.apply {
            smoothScrollToPosition(0)
            adapter = listBimbinganKolokiumViewMhsAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribe()
    }

    private fun subscribe() {
        val statusListener = Observer<String?> { message ->
            Log.i("TAG", "subscribe: $message")
            if (message == "Bimbingan Kolokium create successfully!") {
                kolokiumTabPemb2ViewModel.getListbBimbinganKolokium(token, idKolokium, "Pembimbing 2")
            }
        }
        kolokiumMhsViewModel.getStatus().observe(viewLifecycleOwner, statusListener)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvKolokiumPembimbing2Mhs.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvKolokiumPembimbing2Mhs.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }
}