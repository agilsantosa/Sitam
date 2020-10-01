package com.example.sitam.ui.proposal.mhs

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.databinding.ActivityDetailBimbinganProposalMhsBinding
import com.example.sitam.ui.proposal.mhs.adapter.ListBimbinganProposalMhsAdapter
import com.example.sitam.ui.proposal.mhs.viewmodel.DetailProposalViewModel
import com.example.sitam.ui.proposal.mhs.viewmodel.ProposalViewModelFactory
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.activity_detail_bimbingan_proposal_mhs.*
import java.util.Arrays.sort
import java.util.Collections.sort

class DetailBimbinganProposalMhsActivity : AppCompatActivity() {
    private val filePickerRequest = 101
    lateinit var detailProposalViewModel: DetailProposalViewModel
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private lateinit var listBimbinganAdapter: ListBimbinganProposalMhsAdapter
    private lateinit var token: String
    private lateinit var idProposal: String
    private lateinit var binding: ActivityDetailBimbinganProposalMhsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_detail_bimbingan_proposal_mhs)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        val detailProposalFactory =
            ProposalViewModelFactory(application)
        detailProposalViewModel =
            ViewModelProvider(this, detailProposalFactory)[DetailProposalViewModel::class.java]

        preferenceProvider = SharedPreferenceProvider(applicationContext)
        token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        idProposal = preferenceProvider.getIdProposal(Constants.KEY_ID_PROPOSAL).toString()

        setupRecyclerView()

        detailProposalViewModel.getListBimbinganProposal(token, idProposal.toInt())
        detailProposalViewModel._listBimbinganProposal.observe(this, { response ->
            when (response) {
                is Resource.Succes -> {
                    hideProgressBar()
                    response.data?.let { it ->
                        val data = it.data
                        if (data.isNullOrEmpty()) {
                            binding.emptyText.visibility = View.VISIBLE
                        } else {
                            binding.emptyText.visibility = View.GONE
                            var list = data.sortedByDescending { it.id }
                            listBimbinganAdapter.differ.submitList(list)
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

        binding.fabUpload.setOnClickListener {
            browseDocuments()
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideProgressBar() {
        binding.rvBimbinganProposalmhs.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.stopShimmer()
    }

    private fun showProgressBar() {
        binding.rvBimbinganProposalmhs.visibility = View.GONE
        binding.fragmentClassroomFeedShimmerLayout.visibility = View.VISIBLE
        binding.fragmentClassroomFeedShimmerLayout.startShimmer()
    }

    private fun setupRecyclerView() {
        listBimbinganAdapter = ListBimbinganProposalMhsAdapter()
        rv_bimbinganProposalmhs.apply {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            adapter = listBimbinganAdapter
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == filePickerRequest) {
                if (data != null) {
                    Log.d("pathawal", data.data.toString())
                    val paths = data.data
                    val filename: String? = getRealPathFromUrl(paths!!, this)
                    val filePath = "$paths/$filename"
                    UploadFileConfirmationDialogFragment(paths, filename!!).show(
                        supportFragmentManager,
                        "UploadFragment"
                    )
                }
            }
        }
    }

    fun getRealPathFromUrl(uri: Uri, activity: Activity): String? {
        val cursor = activity.contentResolver.query(uri, null, null, null, null)
        return if (cursor == null) {
            uri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            cursor.getString(index)
        }
    }

    private fun browseDocuments() {
        val mimeTypes = arrayOf(
            "application/msword",
            "application/pdf",
        )
        var intent = Intent()
        intent = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        } else {
            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI)
        }
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("return-data", true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), filePickerRequest)
    }


    internal var onUploadDialog: UploadFileConfirmationDialogFragment.OnUploadDialogListener =
        object : UploadFileConfirmationDialogFragment.OnUploadDialogListener {
            override fun onMessageUpload(text: String) {
                Toast.makeText(this@DetailBimbinganProposalMhsActivity, text, Toast.LENGTH_SHORT)
                    .show()
                if (text.equals("Bimbingan Proposal added successfully!")) {
                    detailProposalViewModel.getListBimbinganProposal(token, idProposal.toInt())
                }
            }
        }
}