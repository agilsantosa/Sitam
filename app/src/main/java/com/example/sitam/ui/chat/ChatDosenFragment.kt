package com.example.sitam.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sitam.R
import com.example.sitam.ui.chat.adapter.PesanMhsAdapter
import com.example.sitam.databinding.FragmentChatDosenBinding
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider

class ChatDosenFragment : Fragment() {

    private var _binding: FragmentChatDosenBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var pesanMhsAdapter: PesanMhsAdapter
    private lateinit var preferenceProvider: SharedPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_dosen, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER)
        val identifier = preferenceProvider.getIdentifierUser(Constants.KEY_IDENTIFIE_USER)


        val chatViewModelFactory = ChatViewModelFactory(requireActivity().application)
        chatViewModel = ViewModelProvider(this, chatViewModelFactory).get(ChatViewModel::class.java)
        chatViewModel.getPesanDosen(token!!, identifier!!)

        setupRecyclerView()

        binding.btnKirimPesan.setOnClickListener {
            val npm = binding.tiPenerima.editText?.text.toString()
            val pesan = binding.tiIsiPesan.editText?.text.toString()

            chatViewModel.sendPesanDosen(token, identifier, npm, pesan)
        }

        chatViewModel.sendChatDosen.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Succes -> {
                    binding.progresBarChatDosen.visibility = View.GONE
                    response.data?.let {
                        showToast(it.message)
                    }
                }
                is Resource.Error -> {
                    binding.progresBarChatDosen.visibility = View.GONE
                    response.data?.let {
                        showToast(it.message)
                    }
                }
                is Resource.Loading -> {
                    binding.progresBarChatDosen.visibility = View.VISIBLE
                }
            }
        })

        chatViewModel.pesanDosen.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    response.data?.let {
                        val data = it.data
                        when (it.message) {
                            "Message not available!" -> binding.emptyText.visibility = View.VISIBLE
                            "Message available!" -> {
                                binding.emptyText.visibility = View.GONE
                                pesanMhsAdapter.differ.submitList(data.sortedByDescending { it.id })
                            }
                            else -> showToast(it.message)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        showToast(it)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.rvChatDosen.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.rvChatDosen.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }

    private fun setupRecyclerView() {
        pesanMhsAdapter = PesanMhsAdapter()
        binding.rvChatDosen.apply {
            adapter = pesanMhsAdapter
        }
    }
}