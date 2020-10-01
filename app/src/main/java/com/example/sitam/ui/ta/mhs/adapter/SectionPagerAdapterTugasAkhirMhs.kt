package com.example.sitam.ui.ta.mhs.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.sitam.ui.ta.mhs.TugasAkhirMhsTabPembimbing1Fragment
import com.example.sitam.ui.ta.mhs.TugasAkhirMhsTabPembimbing2Fragment

class SectionPagerAdapterTugasAkhirMhs(fm: FragmentManager, private val numberOftab: Int) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> TugasAkhirMhsTabPembimbing1Fragment()
            1 -> TugasAkhirMhsTabPembimbing2Fragment()
            else -> throw Exception()
        }
    }

    override fun getCount(): Int {
        return 2
    }

}