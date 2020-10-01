package com.example.sitam.ui.seminar.mhs

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
import com.example.sitam.databinding.ActivitySeminarMahasiswaBinding
import com.example.sitam.ui.seminar.mhs.adapter.SectionsPagerAdapterSeminarMhs
import com.example.sitam.ui.seminar.mhs.viewmodel.SeminarMhsViewModel
import com.google.android.material.tabs.TabLayout

class SeminarMahasiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeminarMahasiswaBinding
    private val filePickerRequest = 101
    private lateinit var sectionsPagerAdapterSeminarMhs: SectionsPagerAdapterSeminarMhs
    private lateinit var seminarMhsViewModel: SeminarMhsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seminar_mahasiswa)

        setupView()

        seminarMhsViewModel = ViewModelProvider(this)[SeminarMhsViewModel::class.java]

        binding.fabCreateSeminar.setOnClickListener {
            handleOnFabClicked(binding.tabSeminarPemdanPenguji.selectedTabPosition)
        }
    }

    private fun setupView() {
        setupToolbar()
        setupViewPager()

    }

    private fun setupViewPager() {
        sectionsPagerAdapterSeminarMhs = SectionsPagerAdapterSeminarMhs(
            supportFragmentManager,
            binding.tabSeminarPemdanPenguji.tabCount
        )
        binding.viewPagerSeminarMhs.adapter = sectionsPagerAdapterSeminarMhs

        binding.tabSeminarPemdanPenguji.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPagerSeminarMhs.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }


        })
        binding.viewPagerSeminarMhs.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                binding.tabSeminarPemdanPenguji
            )
        )
        binding.viewPagerSeminarMhs.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_IDLE -> {
                        binding.fabCreateSeminar.show()
                    }
                    ViewPager.SCROLL_STATE_DRAGGING -> {
                        binding.fabCreateSeminar.hide()
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
            0 -> {
                browseDocuments()
            }
            1 -> {
                browseDocuments()
            }
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

                    UploadFileSeminarDialogFragment(
                        paths,
                        filename!!,
                        binding.tabSeminarPemdanPenguji.selectedTabPosition
                    ).show(
                        supportFragmentManager,
                        "UploadSeminar"
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

    internal var onUploadDialog: UploadFileSeminarDialogFragment.OnUploadSeminarDialogListener =
        object : UploadFileSeminarDialogFragment.OnUploadSeminarDialogListener {
            override fun onMessageUpload(text: String) {
                seminarMhsViewModel.setStatus(text)
            }
        }
}