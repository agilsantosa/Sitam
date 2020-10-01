package com.example.sitam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.sitam.repository.SitamRepository
import com.example.sitam.ui.home.HomeMhsViewModel
import com.example.sitam.ui.home.HomeMhsViewModelFactory
import com.example.sitam.ui.proposal.mhs.viewmodel.ProposalViewModel
import com.example.sitam.ui.proposal.mhs.viewmodel.ProposalViewModelFactory
import com.example.sitam.utils.Constants
import com.example.sitam.utils.SharedPreferenceProvider
import com.example.sitam.utils.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var currentNavController: LiveData<NavController>? = null
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private lateinit var navGraphIds: List<Int>
    lateinit var viewmodel: HomeMhsViewModel
    lateinit var proposalViewModel: ProposalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferenceProvider = SharedPreferenceProvider(applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER)
        val identifier = preferenceProvider.getIdentifierUser(Constants.KEY_IDENTIFIE_USER)
        val level = preferenceProvider.getLevelUser(Constants.KEY_LEVEL_USER)

        val sitamRepository = SitamRepository()
        //homeviewmodel
        val viewModelFactory =
            HomeMhsViewModelFactory(application, sitamRepository, token!!, identifier!!, level!!)
        viewmodel =
            ViewModelProvider(this, viewModelFactory).get(HomeMhsViewModel::class.java)

        val proposalViewModelFactory =
            ProposalViewModelFactory(application)
        proposalViewModel =
            ViewModelProvider(this, proposalViewModelFactory).get(ProposalViewModel::class.java)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }


    private fun setupBottomNavigationBar() {
        val levelUser = preferenceProvider.getLevelUser(Constants.KEY_LEVEL_USER)

        when {
            levelUser.equals("mahasiswa") -> navGraphIds = listOf(
                R.navigation.home_nav,
                R.navigation.proposal_nav,
                R.navigation.ta_nav,
                R.navigation.chat_nav
            )
            levelUser.equals("dosen") -> navGraphIds = listOf(
                R.navigation.home_nav_dosen,
                R.navigation.proposal_nav_dosen,
                R.navigation.ta_nav_dosen,
                R.navigation.chat_nav_dosen
            )

        }

        val controller = main_activity_bottom_navigation_bar.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.main_activity_fragment_container,
            intent = intent
        )

        currentNavController = controller
    }
}