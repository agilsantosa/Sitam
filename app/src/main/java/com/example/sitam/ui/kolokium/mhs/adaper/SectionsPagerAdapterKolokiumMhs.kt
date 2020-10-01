package com.example.sitam.ui.kolokium.mhs.adaper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.sitam.ui.kolokium.mhs.KolokiumTabPemb1Fragment
import com.example.sitam.ui.kolokium.mhs.KolokiumTabPemb2Fragment
import com.example.sitam.ui.kolokium.mhs.KolokiumTabPenguji1Fragment
import com.example.sitam.ui.kolokium.mhs.KolokiumTabPenguji2Fragment

class SectionsPagerAdapterKolokiumMhs(fm: FragmentManager, private val numberOftab: Int) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> KolokiumTabPemb1Fragment()
            1 -> KolokiumTabPemb2Fragment()
            2 -> KolokiumTabPenguji1Fragment()
            3 -> KolokiumTabPenguji2Fragment()
            else -> throw Exception()
        }
    }

    override fun getCount(): Int {
        return 4
    }
}