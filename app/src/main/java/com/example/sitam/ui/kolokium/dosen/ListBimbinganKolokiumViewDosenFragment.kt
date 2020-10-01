package com.example.sitam.ui.kolokium.dosen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sitam.R
import com.example.sitam.databinding.FragmentListBimbinganKolokiumViewDosenBinding
import com.example.sitam.ui.kolokium.dosen.adapter.ListBimbinganKolokiumViewDosenAdapter
import com.example.sitam.ui.kolokium.dosen.viewmodel.KolokiumTabDosenViewModel
import com.example.sitam.ui.seminar.dosen.adapter.ListBimbinganSeminarViewDosenAdapter
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.fragment_list_bimbingan_proposal_dosen.*

class ListBimbinganKolokiumViewDosenFragment : Fragment() {

    private var _binding: FragmentListBimbinganKolokiumViewDosenBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private val kolokiumTabDosenViewModel: KolokiumTabDosenViewModel by viewModels()
    private lateinit var  listBimbinganKolokiumViewDosenAdapter: ListBimbinganKolokiumViewDosenAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list_bimbingan_kolokium_view_dosen,
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
        val id = preferenceProvider.getIdKolokium(Constants.KEY_ID_KOLOKIUM).toString()
        val alias = preferenceProvider.getAlias(Constants.KEY_ALIAS).toString()
        Log.i("TAG", "onViewCreatedKolokium: $id, $alias ")
        kolokiumTabDosenViewModel.getListBimbinganKolokiumDosen(token, id, alias)
        kolokiumTabDosenViewModel.requestListBimbinganKolokiumDosen.observe(viewLifecycleOwner, { response ->
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
                            listBimbinganKolokiumViewDosenAdapter.differ.submitList(data.sortedByDescending { it.id })
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
            findNavController().navigate(R.id.action_listBimbinganKolokiumViewDosenFragment_to_tugasAkhirDosenFragment)
        }

        listBimbinganKolokiumViewDosenAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("dataListBimbingan", it)
            }
            findNavController().navigate(R.id.action_listBimbinganKolokiumViewDosenFragment_to_detailBimbinganKolokiumViewDosenFragment, bundle)
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
        listBimbinganKolokiumViewDosenAdapter = ListBimbinganKolokiumViewDosenAdapter()
        binding.rvBimbinganKolokiumdosenview.apply {
            adapter = listBimbinganKolokiumViewDosenAdapter
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvBimbinganKolokiumdosenview.visibility = View.VISIBLE
        binding.shimmerLayoutListBimbinganKolokium.visibility = View.GONE
        binding.shimmerLayoutListBimbinganKolokium.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvBimbinganKolokiumdosenview.visibility = View.GONE
        binding.shimmerLayoutListBimbinganKolokium.visibility = View.VISIBLE
        binding.shimmerLayoutListBimbinganKolokium.startShimmer()
    }
}