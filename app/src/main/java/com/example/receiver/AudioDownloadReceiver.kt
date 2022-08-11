package com.example.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AudioDownloadReceiver : BroadcastReceiver() {

    var onDownloadCompleted: ((Long?) -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        onDownloadCompleted!!.invoke(intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1))
    }
}
