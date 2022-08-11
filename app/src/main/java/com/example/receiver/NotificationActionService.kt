package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.utils.Constants.ACTION_NAME

class NotificationActionService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.sendBroadcast(Intent("TRACKS_TRACKS").putExtra(ACTION_NAME, intent?.action))
    }
}