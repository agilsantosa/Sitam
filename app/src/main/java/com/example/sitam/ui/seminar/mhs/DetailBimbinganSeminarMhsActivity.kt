package com.example.sitam.ui.seminar.mhs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.sitam.R
import com.example.sitam.databinding.ActivityDetailBimbinganSeminarMhsBinding
import com.example.sitam.models.seminar.DataBimbinganSeminar
import com.example.sitam.ui.seminar.mhs.viewmodel.DetailBimbinganSeminarMhsViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.activity_detail_bimbingan_proposal_mhs.*

class DetailBimbinganSeminarMhsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBimbinganSeminarMhsBinding
    private val detailBimbinganSeminarMhsViewModel: DetailBimbinganSeminarMhsViewModel by viewModels()
    private lateinit var preferenceProvider: SharedPreferenceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_detail_bimbingan_seminar_mhs)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        val data = intent.getParcelableExtra("ID_SEMINAR") as DataBimbinganSeminar

        binding.tvDetailBimbinganSeminarCreateMhs.text = data.created_at
        binding.tvDetailBimbinganSeminarFileNameMhs.text = data.file_revisi
        binding.tvDetailBimbinganSeminarCatatanMhs.text = data.catatan

        preferenceProvider = SharedPreferenceProvider(applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()

        detailBimbinganSeminarMhsViewModel.getReplyBimbinganSeminar(token, data.id.toString())
        detailBimbinganSeminarMhsViewModel.detailSeminar.observe(this, { response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    response.data?.let {
                        val listData = it.data
                        try {
                            val detailSeminar = listData[0]
                            binding.tvDetailBimbinganSeminarCatatanDosen.text =
                                detailSeminar.catatan
                            binding.tvDetailBimbinganSeminarStatusMhs.text = detailSeminar.status
                        } catch (e: IndexOutOfBoundsException) {
                            binding.tvDetailBimbinganSeminarStatusMhs.text = data.status
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        showToast(message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.tablelayoutDetailBimbSeminar.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.tablelayoutDetailBimbSeminar.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }
}