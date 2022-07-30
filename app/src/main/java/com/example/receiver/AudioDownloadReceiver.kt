package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AudioDownloadReceiver : BroadcastReceiver() {

    lateinit var onDownloadCompleted: (() -> Unit)

    override fun onReceive(p0: Context?, p1: Intent?) {
        onDownloadCompleted.invoke()
    }
}