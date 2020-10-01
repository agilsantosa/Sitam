package com.example.sitam.ui.kolokium.mhs

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.sitam.R
import com.example.sitam.databinding.ActivityKolokiumMahasiswaBinding
import com.example.sitam.ui.kolokium.mhs.adaper.SectionsPagerAdapterKolokiumMhs
import com.example.sitam.ui.kolokium.mhs.viewmodel.KolokiumMhsViewModel
import com.google.android.material.tabs.TabLayout

class KolokiumMahasiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKolokiumMahasiswaBinding
    private lateinit var sectionsPagerAdapterKolokiumMhs: SectionsPagerAdapterKolokiumMhs
    private val filePickerRequest = 101
    private lateinit var kolokiumMhsViewModel: KolokiumMhsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_kolokium_mahasiswa)

        setupView()

        kolokiumMhsViewModel = ViewModelProvider(this)[KolokiumMhsViewModel::class.java]

        binding.fabCreateKolokium.setOnClickListener {
            handleOnFabClicked(binding.tabKolokiumMhs.selectedTabPosition)
        }

    }

    private fun setupView() {
        setupToolbar()
        setupViewPager()

    }

    private fun setupViewPager() {
        sectionsPagerAdapterKolokiumMhs = SectionsPagerAdapterKolokiumMhs(
            supportFragmentManager,
            binding.tabKolokiumMhs.tabCount
        )
        binding.viewPagerKolokiumMhs.adapter = sectionsPagerAdapterKolokiumMhs

        binding.tabKolokiumMhs.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPagerKolokiumMhs.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }


        })
        binding.viewPagerKolokiumMhs.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                binding.tabKolokiumMhs
            )
        )
        binding.viewPagerKolokiumMhs.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_IDLE -> {
                        binding.fabCreateKolokium.show()
                    }
                    ViewPager.SCROLL_STATE_DRAGGING -> {
                        binding.fabCreateKolokium.hide()
                    }
                }
            }
        })
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

    private fun handleOnFabClicked(tabPosition: Int) {
        when (tabPosition) {
            0 -> browseDocuments()
            1 -> browseDocuments()
            2 -> browseDocuments()
            3 -> browseDocuments()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == filePickerRequest) {
                if (data != null) {
                    Log.d("pathawal", data.data.toString())
                    val paths = data.data
                    val filename: String? = getRealPathFromUrl(paths!!, this)
                    val filePath = "$paths/$filename"
                    UploadFileKolokiumDialogFragment(
                        paths,
                        filename!!,
                        binding.tabKolokiumMhs.selectedTabPosition
                    ).show(
                        supportFragmentManager,
                        "UploadKolokium"
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

    internal var onUploadDialog: UploadFileKolokiumDialogFragment.OnUploadKolokiumDialogListener =
        object : UploadFileKolokiumDialogFragment.OnUploadKolokiumDialogListener {
            override fun onMessageUpload(text: String) {
                kolokiumMhsViewModel.setStatus(text)
            }
        }
}