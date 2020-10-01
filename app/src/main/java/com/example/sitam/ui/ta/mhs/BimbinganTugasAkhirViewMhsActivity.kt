package com.example.sitam.ui.ta.mhs

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.example.sitam.R
import com.example.sitam.databinding.ActivityBimbinganTugasAkhirViewMhsBinding
import com.example.sitam.ui.seminar.mhs.UploadFileSeminarDialogFragment
import com.example.sitam.ui.seminar.mhs.adapter.SectionsPagerAdapterSeminarMhs
import com.example.sitam.ui.ta.mhs.adapter.SectionPagerAdapterTugasAkhirMhs
import com.example.sitam.ui.ta.mhs.viewmodel.TugasAkhirMahasiswaViewModel
import com.google.android.material.tabs.TabLayout

class BimbinganTugasAkhirViewMhsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBimbinganTugasAkhirViewMhsBinding
    private lateinit var sectionPagerAdapterTugasAkhirMhs : SectionPagerAdapterTugasAkhirMhs
    private val filePickerRequest = 101
    private val tugasAkhirMahasiswaViewModel: TugasAkhirMahasiswaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bimbingan_tugas_akhir_view_mhs)

        setupView()

        binding.fabBimbinganTa.setOnClickListener {
            handleOnFabClicked(binding.tabBimbinganTa.selectedTabPosition)
        }
    }

    private fun handleOnFabClicked(tabPosition: Int) {
        when (tabPosition) {
            0 -> browseDocuments()
            1 -> browseDocuments()
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
                    UploadFileTaDialogFragment(
                        paths,
                        filename!!,
                        binding.tabBimbinganTa.selectedTabPosition
                    ).show(
                        supportFragmentManager,
                        "UploadTa"
                    )
                }
            }
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

    private fun setupView() {
        setupToolbar()
        setupViewPager()

    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupViewPager() {
        sectionPagerAdapterTugasAkhirMhs = SectionPagerAdapterTugasAkhirMhs(
            supportFragmentManager,
            binding.tabBimbinganTa.tabCount
        )
        binding.viewPagerTugasAkhirMhs.adapter = sectionPagerAdapterTugasAkhirMhs

        binding.tabBimbinganTa.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPagerTugasAkhirMhs.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }


        })
        binding.viewPagerTugasAkhirMhs.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                binding.tabBimbinganTa
            )
        )
        binding.viewPagerTugasAkhirMhs.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_IDLE -> {
                        binding.fabBimbinganTa.show()
                    }
                    ViewPager.SCROLL_STATE_DRAGGING -> {
                        binding.fabBimbinganTa.hide()
                    }
                }
            }
        })
    }

    internal var onUploadDialog: UploadFileTaDialogFragment.OnUploadTugasAkhirDialogListener =
        object : UploadFileTaDialogFragment.OnUploadTugasAkhirDialogListener {
            override fun onMessageUpload(text: String) {
                tugasAkhirMahasiswaViewModel.setStatus(text)
                Toast.makeText(this@BimbinganTugasAkhirViewMhsActivity, text, Toast.LENGTH_SHORT)
                    .show()
            }
        }
}