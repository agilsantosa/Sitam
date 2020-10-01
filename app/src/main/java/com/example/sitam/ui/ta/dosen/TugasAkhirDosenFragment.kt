package com.example.sitam.ui.ta.dosen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.sitam.R
import com.example.sitam.databinding.FragmentProposalDosenBinding
import com.example.sitam.databinding.FragmentTugasAkhirDosenBinding
import com.example.sitam.ui.proposal.dosen.adapter.SectionAdapterPager
import com.example.sitam.ui.ta.dosen.adapter.SectionsAdapterTaPager

class TugasAkhirDosenFragment : Fragment() {
    private var _binding: FragmentTugasAkhirDosenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_tugas_akhir_dosen, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sectionPagerAdapter = context?.let { SectionsAdapterTaPager(it, childFragmentManager) }
        binding.viewPagerTaAndKolokium.adapter = sectionPagerAdapter
        binding.tabTaDanKolokium.setupWithViewPager(binding.viewPagerTaAndKolokium)
    }
}