package com.example.sitam.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.sitam.MainActivity
import com.example.sitam.R
import com.example.sitam.databinding.FragmentHomeDosenBinding
import com.example.sitam.databinding.FragmentHomeMahasiswaBinding
import com.example.sitam.ui.auth.LoginActivity
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.fragment_home_mahasiswa.*

class HomeDosenFragment : Fragment() {
    private var _binding: FragmentHomeDosenBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferenceProvider: SharedPreferenceProvider
    private lateinit var homeMhsViewModel: HomeMhsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_dosen, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeMhsViewModel = (activity as MainActivity).viewmodel
        sharedPreferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)

        homeMhsViewModel.profileDosen.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    response.data?.let {
                        val data = it.data
                        binding.tvName.text = data.nama
                        binding.tvNid.text = data.nidn.toString()
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

        binding.btnLogout.setOnClickListener {
            sharedPreferenceProvider.clearSharedPreference()
            val intent = Intent(activity, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
        }.apply {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar() {
        binding.coverShimmerVeil.visibility = View.VISIBLE
        binding.homeFeedShimmerLayout.visibility = View.VISIBLE
        binding.homeFeedShimmerLayout.startShimmer()
    }

    private fun hideProgressBar() {
        binding.coverShimmerVeil.visibility = View.GONE
        binding.homeFeedShimmerLayout.visibility = View.GONE
        binding.homeFeedShimmerLayout.stopShimmer()
    }
}