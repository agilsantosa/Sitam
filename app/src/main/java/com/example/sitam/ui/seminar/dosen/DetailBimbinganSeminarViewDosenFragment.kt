package com.example.sitam.ui.seminar.dosen

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sitam.R
import com.example.sitam.databinding.FragmentDetailBimbinganSeminarViewDosenBinding
import com.example.sitam.service.DownloadSeminarService
import com.example.sitam.ui.proposal.dosen.viewmodel.DetailBimbinganProposalViewModel
import com.example.sitam.ui.seminar.dosen.viewmodel.DetailBimbinganSeminarViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.fragment_detail_bimbingan_seminar_view_dosen.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailBimbinganSeminarViewDosenFragment : Fragment() {

    private val args: DetailBimbinganSeminarViewDosenFragmentArgs by navArgs()
    private var _binding: FragmentDetailBimbinganSeminarViewDosenBinding? = null
    private val binding get() = _binding!!
    private lateinit var downloadReceiver: BroadcastReceiver
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private val detailBimbinganSeminarViewModel: DetailBimbinganSeminarViewModel by viewModels()

    companion object {
        const val ACTION_DOWNLOAD_STATUS = "download_status_seminar"
        private const val SMS_REQUEST_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail_bimbingan_seminar_view_dosen,
            container,
            false
        )

        downloadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d(DownloadSeminarService.TAG, "Download Selesai")
                Toast.makeText(context, "Download Selesai", Toast.LENGTH_SHORT).show()
            }
        }
        val downloadIntentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
//        registerReceiver(downloadReceiver, downloadIntentFilter)
        activity?.registerReceiver(downloadReceiver, downloadIntentFilter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()

        val data = args.datalistBimbingan

        binding.tvCreateDetailBimbingan.text = data.created_at
        binding.tvCatatanMhs.text = data.catatan

        val items = arrayOf(
            "Approved",
            "Revisi"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, items
        )
        binding.materialSpinnerItem.setAdapter<ArrayAdapter<String>>(adapter)

        binding.materialSpinnerItem.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.materialSpinnerItem.text.toString().equals("Approved")) {
                    binding.tiNilaiSeminar.visibility = View.VISIBLE
                } else {
                    binding.tiNilaiSeminar.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_detailBimbinganSeminarViewDosenFragment_to_listBimbinganSeminarViewDosenFragment)
        }

        binding.btnUnduhFile.setOnClickListener {
            if (data.file_revisi.isNullOrEmpty()) {
                Toast.makeText(context, "File tidak ada", Toast.LENGTH_SHORT).show()
            } else {
                val downloadSeminarServiceIntent =
                    Intent(context, DownloadSeminarService::class.java)
                downloadSeminarServiceIntent.putExtra("filename_seminar", data.file_revisi)
//                downloadSeminarServiceIntent.putExtra("flowSeminar", "seminar")
                requireActivity().startService(downloadSeminarServiceIntent)
            }
        }

        binding.btnSimpan.setOnClickListener {
            val idBimbingan = data.id
            val idSeminar = data.id_seminar.toString()
            val by = data.to
            val catatan = binding.tiCatatanDetailBimbingan.editText?.text.toString()
            val nilai = binding.tiNilaiSeminar.editText?.text.toString()
            var status = binding.materialSpinnerItem.text.toString()
            var isEmpty = false

            if (status == "Approved") {
                status = "1"
            } else if (status == "Revisi") {
                status = "0"
            } else {
                status = ""
            }

            if (TextUtils.isEmpty(catatan)) {
                isEmpty = true
                binding.tiCatatanDetailBimbingan.error = "Field tidak boleh kosong"
            }
            binding.tiCatatanDetailBimbingan.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (TextUtils.isEmpty(catatan)) {
                        binding.tiCatatanDetailBimbingan.error = null
                    }
                }

            })

            if (TextUtils.isEmpty(status)) {
                isEmpty = true
                binding.tiStatusSeminar.error = "Field tidak boleh kosong"
            }
            binding.tiStatusSeminar.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (TextUtils.isEmpty(status)) {
                        binding.tiStatusSeminar.error = null
                    }
                }

            })

            if (!isEmpty) {
                detailBimbinganSeminarViewModel.getReplyBimbinganSeminar(
                    token,
                    idSeminar,
                    status,
                    by,
                    idBimbingan,
                    catatan,
                    nilai
                )
                binding.tiCatatanDetailBimbingan.editText?.setText("")
                binding.tiStatusSeminar.editText?.setText("")
                binding.tiNilaiSeminar.editText?.setText("")
            }

            detailBimbinganSeminarViewModel.replySeminar.observe(viewLifecycleOwner, { response ->
                when (response) {
                    is Resource.Succes -> {
                        hideProgressBar()
                        response.data?.let {
                            Log.i("TAG", "onViewCreated: sukses")
                            when (it.message) {
                                "Conversion Error" -> showToast("Bimbingan Seminar Reply Succesfully!")
                                else -> showToast(it.message)
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            Log.i("TAG", "onViewCreated: gagal")
                            when (message) {
                                "Conversion Error" -> showToast("Bimbingan Seminar Reply Succesfully!")
                                else -> showToast(message)
                            }
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            })

            GlobalScope.launch {
                delay(1500)
                findNavController().navigate(R.id.action_detailBimbinganSeminarViewDosenFragment_to_listBimbinganSeminarViewDosenFragment)
            }
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
            supportActionBar?.apply {
                setDisplayShowHomeEnabled(true)
                setDisplayShowTitleEnabled(false)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            val uriString: String = selectedFile.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(downloadReceiver)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == SMS_REQUEST_CODE) {
            when {
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> Toast.makeText(
                    context,
                    "Sms receiver permission diterima",
                    Toast.LENGTH_SHORT
                ).show()
                else -> Toast.makeText(
                    context,
                    "Sms receiver permission ditolak",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar() {
        binding.progressBarSeminar.visibility = View.VISIBLE
        binding.btnSimpan.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.progressBarSeminar.visibility = View.GONE
        binding.btnSimpan.visibility = View.VISIBLE
    }
}