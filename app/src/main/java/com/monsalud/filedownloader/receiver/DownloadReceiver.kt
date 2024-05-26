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

        // Query to check the status of the download
        val query = id?.let { DownloadManager.Query().setFilterById(it) }
        val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cursor = downloadManager.query(query)

        // Check if the Cursor can be moved to the first position
        if (cursor.moveToFirst()) {
            // Get the column index for the status column
            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            // Check if the column index is valid (needs to be greater than or equal to 0)
            if (columnIndex >= 0) {
            val status = cursor.getInt(columnIndex)
                // Check to see if the download is successful. If so, send a notification.
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    val notificationManager = context.let {
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
        }
    }
}
