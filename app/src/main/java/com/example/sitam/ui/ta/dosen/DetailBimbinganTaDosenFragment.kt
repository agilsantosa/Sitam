package com.example.sitam.ui.ta.dosen

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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sitam.R
import com.example.sitam.databinding.FragmentDetailBimbinganTaDosenBinding
import com.example.sitam.service.DownloadServiceTa
import com.example.sitam.ui.ta.dosen.viewmodel.DetailBimbinganTaDosenViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.fragment_list_bimbingan_proposal_dosen.*

class DetailBimbinganTaDosenFragment : Fragment() {

    private var _binding: FragmentDetailBimbinganTaDosenBinding? = null
    private val binding get() = _binding!!
    private val args: DetailBimbinganTaDosenFragmentArgs by navArgs()
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private lateinit var downloadServiceTa: BroadcastReceiver
    private val detailBimbinganTaDosenViewModel: DetailBimbinganTaDosenViewModel by activityViewModels()

    companion object {
        const val ACTION_DOWNLOAD_STATUS = "download_status"
        private const val SMS_REQUEST_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail_bimbingan_ta_dosen,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER)

        val dataTa = args.dataListBimbingan

        binding.tvCreateDetailBimbingan.text = dataTa.created_at
        binding.tvCatatanMhs.text = dataTa.catatan

        downloadServiceTa = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d(DownloadServiceTa.TAG, "Download Selesai")
                Toast.makeText(context, "Download Selesai", Toast.LENGTH_SHORT).show()
            }
        }

        val downloadIntentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
//        registerReceiver(downloadReceiver, downloadIntentFilter)
        requireActivity().registerReceiver(downloadServiceTa, downloadIntentFilter)

        binding.btnUnduhFile.setOnClickListener {
            if (dataTa.file_revisi.isNullOrEmpty()) {
                Toast.makeText(context, "File tidak ada", Toast.LENGTH_SHORT).show()
            } else {
                val downloadServiceIntent = Intent(context, DownloadServiceTa::class.java)
                downloadServiceIntent.putExtra("filename", dataTa.file_revisi)
                requireActivity().startService(downloadServiceIntent)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_detailBimbinganTaDosenFragment_to_listBimbinganTaDosenFragment)
        }

        binding.btnSimpan.setOnClickListener {
            val idBimbingan = dataTa.id
            val idTa = dataTa.id_ta
            val by: String? = dataTa.to
            val catatan = binding.tiCatatanDetailBimbingan.editText?.text.toString()
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
                binding.tiStatusProposal.error = "Field tidak boleh kosong"
            }
            binding.tiStatusProposal.editText?.addTextChangedListener(object : TextWatcher {
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
                        binding.tiStatusProposal.error = null
                    }
                }

            })
            if (!isEmpty) {
                detailBimbinganTaDosenViewModel.getReplyBimbinganTugasAkhir(
                    token!!,
                    idTa.toString(),
                    by ?: "" ,
                    catatan,
                    status,
                    idBimbingan as Int
                )
                binding.tiCatatanDetailBimbingan.editText?.setText("")
                binding.tiStatusProposal.editText?.setText("")
            }

            detailBimbinganTaDosenViewModel.replyTugasAkhir.observe(viewLifecycleOwner, { response ->
                when (response) {
                    is Resource.Succes -> {
                        hideProgressBar()
                        response.data?.let {
                            when (it.message) {
                                "Conversion Error" -> showToast("Bimbingan Ta Reply Succesfully!")
                                "Internal Server Error" -> showToast("Bimbingan Ta Reply Succesfully!")
                                else -> showToast(it.message)
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            when (message) {
                                "Conversion Error" -> showToast("Bimbingan Ta Reply Succesfully!")
                                "Internal Server Error" -> showToast("Bimbingan Ta Reply Succesfully!")
                                else -> showToast(message)
                            }
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }

            })
        }

        val items = arrayOf(
            "Approved",
            "Revisi"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, items
        )
        binding.materialSpinnerItem.setAdapter<ArrayAdapter<String>>(adapter)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            val uriString: String = selectedFile.toString()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar() {
        binding.progressBarTa.visibility = View.VISIBLE
        binding.btnSimpan.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.progressBarTa.visibility = View.GONE
        binding.btnSimpan.visibility = View.VISIBLE
    }
}