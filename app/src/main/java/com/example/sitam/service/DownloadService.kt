package com.example.sitam.service

import android.app.DownloadManager
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.sitam.BuildConfig
import com.example.sitam.ui.proposal.dosen.DetailBimbinganProposalDosenFragment


class DownloadService : IntentService("DownloadService") {

    companion object {
        val TAG = DownloadService::class.java.simpleName
    }

    var myDownloadId: Long = 0

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "Download Service dijalankan")
        if (intent != null) {
            try {
                val filename = intent.getStringExtra("filename")
                val request = DownloadManager.Request(
                    Uri.parse(BuildConfig.URL_BASE + "/proposal/" + filename)
                )
                    .setTitle("Downloading $filename")
                    .setDescription("The File Is Downloading...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)


                val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                myDownloadId = dm.enqueue(request)

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val notifyFinishIntent = Intent(DetailBimbinganProposalDosenFragment.ACTION_DOWNLOAD_STATUS)
            sendBroadcast(notifyFinishIntent)
        }
    }

}
