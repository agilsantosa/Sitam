package com.example.sitam.ui.ta.dosen.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.sitam.R
import com.example.sitam.ui.kolokium.dosen.KolokiumTabDosenFragment
import com.example.sitam.ui.ta.dosen.TugasAkhirTabDosenFragment

class SectionsAdapterTaPager(private val context: Context, fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @StringRes
    private val TABTITLE = arrayOf(R.string.tugas_akhir, R.string.kolokium)

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = TugasAkhirTabDosenFragment()
            1 -> fragment = KolokiumTabDosenFragment()
        }
        return fragment as Fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TABTITLE[position])
    }

    override fun getCount(): Int {
        return 2
    }

}