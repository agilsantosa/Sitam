package com.example.sitam.ui.ta.mhs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sitam.R
import com.example.sitam.databinding.FragmentTugasAkhirMahasiswaBinding
import com.example.sitam.ui.kolokium.mhs.RegisterKolokiumDialogFragment
import com.example.sitam.ui.kolokium.mhs.viewmodel.KolokiumMhsViewModel
import com.example.sitam.ui.ta.mhs.viewmodel.TugasAkhirMahasiswaViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider

class TugasAkhirMahasiswaFragment : Fragment() {
    private var _binding: FragmentTugasAkhirMahasiswaBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private val tugasAkhirMahasiswaViewModel: TugasAkhirMahasiswaViewModel by activityViewModels()
    private val kolokiumMhsViewModel: KolokiumMhsViewModel by activityViewModels()
    private lateinit var token: String
    private lateinit var identifier: String
    private var idKolokium: String = "-"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tugas_akhir_mahasiswa,
            container,
            false
        )
        preferenceProvider = SharedPreferenceProvider(requireActivity().application)
        token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        identifier = preferenceProvider.getIdentifierUser(Constants.KEY_IDENTIFIE_USER).toString()

        tugasAkhirMahasiswaViewModel.getDataTaMhs(token, identifier)
        kolokiumMhsViewModel.getDataKolokium(token, identifier)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeView()
        observeViewKolokium()

        binding.fabDetailTa.setOnClickListener {
            if ((binding.tvPembimbing1.text == "-") and (binding.tvPembimbing2.text == "-")) {
                Toast.makeText(
                    context,
                    "Mohon menunggu... Pembimbing belum ditentukan",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                findNavController().navigate(R.id.action_tugasAkhirMahasiswaFragment2_to_bimbinganTugasAkhirViewMhsActivity)
            }
        }

        binding.fabKolokium.setOnClickListener {
            when{
                idKolokium == "-" -> {
                    Log.i("TAG", "onViewCreated: idKOlokium $idKolokium")
                    val mRegisterKolokiumDialogFragment = RegisterKolokiumDialogFragment()
                    val mFragmentManager = childFragmentManager
                    mRegisterKolokiumDialogFragment.show(
                        mFragmentManager,
                        RegisterKolokiumDialogFragment::class.java.simpleName
                    )
                }
//                binding.tvPenguji1.text == "-" -> {
//                    Toast.makeText(
//                        context,
//                        "Mohon menunggu... Tanggal sidang belum ditentukan",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
                else -> {
                    findNavController().navigate(R.id.action_tugasAkhirMahasiswaFragment2_to_kolokiumMahasiswaActivity)
                }
            }
        }
    }

    private fun observeViewKolokium() {
        kolokiumMhsViewModel.requestDataKolokium.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    response.data?.let {
                        val data = it.data
                        when (it.message) {
                            "Kolokium available!" -> {
                                binding.cardViewTaMhs.visibility = View.VISIBLE
                                idKolokium = data.id.toString()
                                preferenceProvider.saveIdKolokium(Constants.KEY_ID_KOLOKIUM, idKolokium)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
//                        when (message) {
//                            "Not Found" -> hideEmpty()
//                            else -> showToast(message)
//                        }
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun observeView() {
        tugasAkhirMahasiswaViewModel.requestDataTaMhs.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    response.data?.let {
                        val data = it.data
                        when (it.message) {
                            "Ta Not available!" -> {
                                showEmpty()
                            }
                            "Ta availabel!" -> {
                                hideEmpty()
                                binding.cardViewTaMhs.visibility = View.VISIBLE
                                preferenceProvider.saveIdTa(
                                    Constants.KEY_ID_TA,
                                    data.id.toString()
                                )

                                val nilai = data.nilai ?: 0
                                val pembimbing1 = data.pembimbing1 ?: "-"
                                val pembimbing2 = data.pembimbing2 ?: "-"
                                val penguji1 = data.penguji1 ?: "-"
                                val penguji2 = data.penguji1 ?: "-"

                                binding.tvKonsentrasiTa.text = data.konsentrasi
                                binding.tvTopikTa.text = data.topik_tugas_akhir
                                binding.tvTahunPengajuanTa.text = data.tahun_pengajuan.toString()
                                binding.tvStatusTa.text = data.status
                                binding.tvNilaiTa.text = nilai.toString()
                                binding.tvPembimbing1.text = pembimbing1
                                binding.tvPembimbing2.text = pembimbing2
                                binding.tvPenguji1.text = penguji1
                                binding.tvPenguji2.text = penguji2
                            }
                        }

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.i("TAG", "observeView: error")
                        when (message) {
                            "Not Found" -> showEmpty()
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

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar() {
        binding.progressBarTa.visibility = View.VISIBLE
        binding.fabDetailTa.visibility = View.GONE
        binding.fabKolokium.visibility = View.GONE
        binding.cardViewTaMhs.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.tvEmptyTa.visibility = View.VISIBLE
        binding.fabDetailTa.visibility = View.GONE
        binding.fabKolokium.visibility = View.GONE
    }

    private fun hideEmpty() {
        binding.tvEmptyTa.visibility = View.GONE
        binding.fabDetailTa.visibility = View.VISIBLE
        binding.fabKolokium.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBarTa.visibility = View.GONE
//        binding.fabDetailTa.visibility = View.VISIBLE
//        binding.fabKolokium.visibility = View.VISIBLE
    }

    internal var onRegiterKolokiumDialog: RegisterKolokiumDialogFragment.OnRegisterKolokiumDialogListener =
        object : RegisterKolokiumDialogFragment.OnRegisterKolokiumDialogListener {
            override fun onMessageRegister(text: String) {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                if (text.equals("Berhasil mendaftar kolokium!")) {
                    kolokiumMhsViewModel.getDataKolokium(token, identifier)
                }
            }
        }
}