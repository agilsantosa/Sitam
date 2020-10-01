package com.example.sitam.ui.ta.mhs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.sitam.R
import com.example.sitam.databinding.ActivityDetailBimbinganTaMhsBinding
import com.example.sitam.models.seminar.DataBimbinganSeminar
import com.example.sitam.models.ta.DataListBimbinganTaMhs
import com.example.sitam.ui.ta.mhs.viewmodel.DetailBimbinganTaMhsViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.activity_detail_bimbingan_proposal_mhs.*

class DetailBimbinganTaMhsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBimbinganTaMhsBinding
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private val detailBimbinganTaMhsViewModel: DetailBimbinganTaMhsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_detail_bimbingan_ta_mhs)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        val data = intent.getParcelableExtra("ID_TA") as DataListBimbinganTaMhs

        binding.tvDetailBimbinganTaCreateMhs.text = data.created_at
        binding.tvDetailBimbinganTaFileNameMhs.text = data.file_revisi
        binding.tvDetailBimbinganTaCatatanMhs.text = data.catatan

        preferenceProvider = SharedPreferenceProvider(applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()

        detailBimbinganTaMhsViewModel.getReplyBimbinganTa(token, data.id.toString())
        detailBimbinganTaMhsViewModel.detailTugasAkhir.observe(this, { response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    Log.i("TAG", "onCreate idTa: sukses")
                    response.data?.let {
                        val data = it.data
                        binding.tvDetailBimbinganSeminarCatatanDosen.text = data.catatan
                        binding.tvDetailBimbinganSeminarStatusMhs.text = data.status
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Log.i("TAG", "onCreate idTa: gagal")
                    response.message?.let { message ->
                        when (message) {
                            "Not Found" -> binding.tvDetailBimbinganSeminarStatusMhs.text = "Belum ditanggapi"
                            else -> showToast(message)
                        }
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
        binding.tablelayoutDetailBimbTa.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.tablelayoutDetailBimbTa.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }
}