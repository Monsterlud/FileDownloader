package com.monsalud.filedownloader.receiver

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.monsalud.filedownloader.R
import com.monsalud.sendNotification

class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

        val notificationManager = context?.let {
            ContextCompat.getSystemService(
                it,
                NotificationManager::class.java
            )
        } as NotificationManager

        notificationManager.sendNotification(
            context.getText(R.string.file_download_complete).toString(),
            "Udacity: Test Message Body",
            context
        )
    }
}
