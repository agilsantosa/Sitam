package com.example.sitam.ui.kolokium.mhs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.sitam.R
import com.example.sitam.databinding.ActivityDetailBimbinganKolokiumMhsBinding
import com.example.sitam.models.kolokium.DataListBimbinganKolokiumMhs
import com.example.sitam.utils.Constants
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.activity_detail_bimbingan_proposal_mhs.*

class DetailBimbinganKolokiumMhsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBimbinganKolokiumMhsBinding
    private lateinit var preferenceProvider: SharedPreferenceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_bimbingan_kolokium_mhs)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        val data = intent.getParcelableExtra("ID_KOLOKIUM") as DataListBimbinganKolokiumMhs

        binding.tvDetailBimbinganKolokiumCreateMhs.text = data.created_at
        binding.tvDetailBimbinganKolokiumFileNameMhs.text = data.file_revisi
        binding.tvDetailBimbinganKolokiumCatatanMhs.text = data.catatan

        preferenceProvider = SharedPreferenceProvider(applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.tablelayoutDetailBimbKolokium.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.tablelayoutDetailBimbKolokium.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }
}