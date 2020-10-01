package com.example.sitam.ui.seminar.mhs.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.sitam.ui.seminar.mhs.SeminarTabPembimbingFragment
import com.example.sitam.ui.seminar.mhs.SeminarTabPengujiFragment

class SectionsPagerAdapterSeminarMhs(fm: FragmentManager, private val numberOftab: Int) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SeminarTabPembimbingFragment()
            1 -> SeminarTabPengujiFragment()
            else -> throw Exception()
        }
    }

    override fun getCount(): Int {
        return 2
    }

}