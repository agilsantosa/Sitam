package com.example.sitam.ui.kolokium.dosen

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
import com.example.sitam.databinding.FragmentDetailBimbinganKolokiumViewDosenBinding
import com.example.sitam.service.DownloadKolokiumService
import com.example.sitam.ui.kolokium.dosen.viewmodel.DetailBimbinganKolokiumViewDosenViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.fragment_detail_bimbingan_seminar_view_dosen.*

class DetailBimbinganKolokiumViewDosenFragment : Fragment() {

    private val args: DetailBimbinganKolokiumViewDosenFragmentArgs by navArgs()
    private var _binding: FragmentDetailBimbinganKolokiumViewDosenBinding? = null
    private val binding get() = _binding!!
    private lateinit var downloadReceiver: BroadcastReceiver
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private val detailBimbinganKolokiumViewDosenViewModel: DetailBimbinganKolokiumViewDosenViewModel by viewModels()

    companion object {
        const val ACTION_DOWNLOAD_STATUS = "download_status"
        private const val SMS_REQUEST_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_detail_bimbingan_kolokium_view_dosen,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()

        val data = args.dataListBimbingan

        binding.tvCreateDetailBimbingan.text = data.created_at
        binding.tvCatatanMhs.text = data.catatan

        downloadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d(DownloadKolokiumService.TAG, "Download Selesai")
                Toast.makeText(context, "Download Selesai", Toast.LENGTH_SHORT).show()
            }
        }

        val downloadIntentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
//        registerReceiver(downloadReceiver, downloadIntentFilter)
        requireActivity().registerReceiver(downloadReceiver, downloadIntentFilter)

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
                    binding.tiNilaiKolokium.visibility = View.VISIBLE
                } else {
                    binding.tiNilaiKolokium.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_detailBimbinganKolokiumViewDosenFragment_to_listBimbinganKolokiumViewDosenFragment)
        }

        binding.btnUnduhFile.setOnClickListener {
            if (data.file_revisi.isNullOrEmpty()) {
                Toast.makeText(context, "File tidak ada", Toast.LENGTH_SHORT).show()
            } else {
                val downloadKolokiumServiceIntent =
                    Intent(context, DownloadKolokiumService::class.java)
                downloadKolokiumServiceIntent.putExtra("filename", data.file_revisi)
                downloadKolokiumServiceIntent.putExtra("flowKolokium", "kolokium")
                requireActivity().startService(downloadKolokiumServiceIntent)
            }
        }

        binding.btnSimpan.setOnClickListener {
            val idBimbingan = data.id
            val idKolokium = data.id_kolokium.toString()
            val by = data.to
            val catatan = binding.tiCatatanDetailBimbingan.editText?.text.toString()
            val nilai = binding.tiNilaiKolokium.editText?.text.toString()
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
                binding.tiStatusKolokium.error = "Field tidak boleh kosong"
            }
            binding.tiStatusKolokium.editText?.addTextChangedListener(object : TextWatcher {
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
                        binding.tiStatusKolokium.error = null
                    }
                }

            })

            if (TextUtils.isEmpty(nilai)) {
                isEmpty = true
                binding.tiNilaiKolokium.error = "Field tidak boleh kosong"
            }
            binding.tiNilaiKolokium.editText?.addTextChangedListener(object : TextWatcher {
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
                        binding.tiNilaiKolokium.error = null
                    }
                }

            })

            if (!isEmpty) {
//                Toast.makeText(activity, "id = $idBimbingan, status = $status, by = $by, idKol = $idKolokium," +
//                        "status = $status, catatan =  $catatan, nilai = $nilai", Toast.LENGTH_SHORT).show()
                detailBimbinganKolokiumViewDosenViewModel.getReplyBimbinganKolokium(
                    token,
                    idKolokium,
                    status,
                    by,
                    idBimbingan,
                    catatan,
                    nilai
                )
                binding.tiCatatanDetailBimbingan.editText?.setText("")
                binding.tiStatusKolokium.editText?.setText("")
                binding.tiNilaiKolokium.editText?.setText("")
            }

            detailBimbinganKolokiumViewDosenViewModel.replyKolokium.observe(viewLifecycleOwner, { response ->
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
        requireActivity().unregisterReceiver(downloadReceiver)
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
        binding.progressBarKolokium.visibility = View.VISIBLE
        binding.btnSimpan.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.progressBarKolokium.visibility = View.GONE
        binding.btnSimpan.visibility = View.VISIBLE
    }

}