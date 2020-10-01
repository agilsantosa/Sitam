package com.example.sitam.ui.proposal.dosen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.sitam.R
import com.example.sitam.databinding.FragmentProposalDosenBinding
import com.example.sitam.ui.proposal.dosen.adapter.SectionAdapterPager
import kotlinx.android.synthetic.main.fragment_proposal_dosen.*


class ProposalDosenFragment : Fragment() {

    private var _binding: FragmentProposalDosenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_proposal_dosen, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //tablyaout proposal and seminar
        val sectionPagerAdapter = context?.let { SectionAdapterPager(it, childFragmentManager) }
        binding.viewPagerProposalAndSeminar.adapter = sectionPagerAdapter
        binding.tabProposalAndSeminar.setupWithViewPager(binding.viewPagerProposalAndSeminar)
    }
}