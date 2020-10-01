package com.example.sitam.ui.proposal.dosen.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.sitam.R
import com.example.sitam.ui.proposal.dosen.ProposalTabDosenFragment
import com.example.sitam.ui.seminar.dosen.SeminarTabDosenFragment

class SectionAdapterPager(private val context: Context, fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @StringRes
    private val TABTITLE = arrayOf(R.string.tab_proposal, R.string.tab_seminar)

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = ProposalTabDosenFragment()
            1 -> fragment = SeminarTabDosenFragment()
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