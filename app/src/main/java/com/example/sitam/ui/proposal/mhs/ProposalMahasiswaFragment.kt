package com.example.sitam.ui.proposal.mhs

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sitam.MainActivity
import com.example.sitam.R
import com.example.sitam.databinding.FragmentProposalMahasiswaBinding
import com.example.sitam.ui.proposal.mhs.viewmodel.ProposalViewModel
import com.example.sitam.ui.seminar.mhs.RegisterSeminarMhsDialogFragment
import com.example.sitam.ui.seminar.mhs.viewmodel.SeminarMhsViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.fragment_login.*

class ProposalMahasiswaFragment : Fragment() {

    private var _binding: FragmentProposalMahasiswaBinding? = null
    private val binding get() = _binding!!
    private lateinit var proposalViewModel: ProposalViewModel
    private var isOpen: Boolean = false
    private var isOpenFab: Boolean = false
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private val seminarMhsViewModel: SeminarMhsViewModel by activityViewModels()
    private lateinit var token: String
    private lateinit var identifier: String
    private var idSeminar: String = ""

    companion object {
        private const val TARGET_FRAGMENT_REQUEST_CODE = 100
        private const val EXTRA_GREETING_MESSAGE = "message"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("TAG", "onCreateView: dipanggil")
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_proposal_mahasiswa,
            container,
            false
        )

        preferenceProvider = SharedPreferenceProvider(requireActivity().application)
        token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        identifier = preferenceProvider.getIdentifierUser(Constants.KEY_IDENTIFIE_USER).toString()
        val level = preferenceProvider.getLevelUser(Constants.KEY_LEVEL_USER)

        proposalViewModel = (activity as MainActivity).proposalViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("TAG", "onViewCreated: dipanggil")
        super.onViewCreated(view, savedInstanceState)

        proposalViewModel.getProposalMhs(token, identifier)
        seminarMhsViewModel.getDataSeminar(token, identifier)

        observeView()
        observeSeminar()
        binding.fabProposal.setOnClickListener {
            when {
                !isOpenFab -> {
                    findNavController().navigate(R.id.action_proposalMahasiswaFragment2_to_addProposalFragment)
                }
                isOpenFab -> {
                    if (isOpen) hidefabFull()
                    else showfabFull()
                }
            }
        }

        binding.fabSeminar.setOnClickListener {
            when {
                idSeminar.isNullOrEmpty() -> {
                    val mRegisterSeminarDialogFragment = RegisterSeminarMhsDialogFragment()
                    val mFragmentManager = childFragmentManager
                    mRegisterSeminarDialogFragment.show(
                        mFragmentManager,
                        RegisterSeminarMhsDialogFragment::class.java.simpleName
                    )
                }
                binding.tvPenguji.text == "-" -> {
                    Toast.makeText(
                        context,
                        "Mohon menunggu... Tanggal sidang belum ditentukan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    findNavController().navigate(
                        R.id.action_proposalMahasiswaFragment2_to_seminarMahasiswaActivity
                    )
                }
            }
        }

        binding.fabDetailProposal.setOnClickListener {
            if (binding.tvPembimbing.text == "-") {
                Toast.makeText(
                    context,
                    "Mohon menunggu... Pembimbing belum ditentukan",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                findNavController().navigate(R.id.action_proposalMahasiswaFragment2_to_detailBimbinganProposalMhsActivity)
            }
        }
    }

    private fun observeSeminar() {
        seminarMhsViewModel.requestDataSeminar.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    response.data?.let {
                        Log.i("TAG", "observeSeminar: dipanggil")
                        when(it.message){
                            "Seminar availabel!" -> {
                                val data = it.data
                                idSeminar = data.id.toString()
                                preferenceProvider.saveIdSeminar(Constants.KEY_ID_SEMINAR, idSeminar)
                                if(data.approval == "Terdaftar Seminar [Disetujui]") {
                                    binding.tableRowWaktuSeminar.visibility = View.VISIBLE
                                    binding.tvWaktuSeminar.text = data.tanggal_sidang
                                }
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->

                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun observeView() {
        proposalViewModel.proposalMahasiswa.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    response.data?.let {
                        Log.i("TAG", "observeView: ${it.message}")
                        when (it.message) {
                            "Proposal availabel!" -> {
                                binding.cardViewProposalMhs.visibility = View.VISIBLE
                                handleFab(true)
                                val data = it.data
                                preferenceProvider.saveIdProposal(
                                    Constants.KEY_ID_PROPOSAL,
                                    data.id.toString()
                                )
                                binding.tvTopikProposal.text = data.topik
                                binding.tvKonsentrasi.text = data.konsentrasi
                                binding.tvTahunPengajuan.text = data.tahun_pengajuan.toString()
                                binding.tvPembimbing.text = data.pembimbing.nama ?: "-"
                                val status: String = data.status
                                if (status == "-"){
                                    binding.tvStatusProposal.text = "Menunggu Konfirmasi"
                                }else{
                                    binding.tvStatusProposal.text = data.status
                                }
                                binding.tvPenguji.text = data.penguji ?: "-"
                                binding.tvNilai.text = data.nilai.toString()
                                binding.tvJudulProposal.text = data.judul_proposal
                            }
                            else -> showToast(it.message)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {message ->
                        Log.i("TAG", "observeView gagal: $message")
                        when (message) {
                            "Not Found" -> handleFab(false)
                            else -> binding.fabProposal.hide()
                        }
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun showProgressBar() {
        binding.progressBarProposal.visibility = View.VISIBLE
        binding.fabProposal.hide()
    }

    private fun hideProgressBar() {
        binding.progressBarProposal.visibility = View.GONE
        binding.fabProposal.show()
    }

    private fun showfabFull() {
        val animOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        binding.fabDetailProposal.animation = animOpen
        binding.fabSeminar.animation = animOpen

        binding.fabDetailProposal.visibility = View.VISIBLE
        binding.fabSeminar.visibility = View.VISIBLE
        binding.tvSeminar.visibility = View.VISIBLE
        binding.tvDetail.visibility = View.VISIBLE
//        binding.cardViewProposalMhs.visibility = View.VISIBLE
        isOpen = true
    }

    private fun hidefabFull() {
        val animClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        binding.fabDetailProposal.animation = animClose
        binding.fabSeminar.animation = animClose

        binding.fabDetailProposal.visibility = View.GONE
        binding.fabSeminar.visibility = View.GONE
        binding.tvSeminar.visibility = View.GONE
        binding.tvDetail.visibility = View.GONE
//        binding.cardViewProposalMhs.visibility = View.GONE
        isOpen = false
    }

    private fun handleFab(status: Boolean) {
        if (status) {
            isOpenFab = true
            binding.fabProposal.setImageResource(R.drawable.ic_menu)
        } else {
            isOpenFab = false
            hidefabFull()
            binding.cardViewProposalMhs.visibility = View.GONE
            binding.fabProposal.setImageResource(R.drawable.ic_add)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    internal var onRegiterSeminarDialog: RegisterSeminarMhsDialogFragment.OnRegisterSeminarDialogListener =
        object : RegisterSeminarMhsDialogFragment.OnRegisterSeminarDialogListener {
            override fun onMessageRegister(text: String) {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                if (text.equals("Berhasil mendaftar seminar!")) {
                    seminarMhsViewModel.getDataSeminar(token, identifier)
                }
            }
        }
}