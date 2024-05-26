package com.monsalud

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.monsalud.filedownloader.R
import com.monsalud.filedownloader.databinding.ActivityMainBinding
import com.monsalud.filedownloader.receiver.DownloadReceiver
import com.monsalud.filedownloader.ui.ButtonState

private const val NOTIFICATION_ID = 0

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0
    private val downloadReceiver = DownloadReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            getString(R.string.file_downloader_channel_id),
            getString(R.string.file_downloader_channel_name)
        )

        val downloadButton = binding.contentMain.btnDownload
        val radioGlide = binding.contentMain.radioGlide
        val radioLoadApp = binding.contentMain.radioLoadApp
        val radioRetrofit = binding.contentMain.radioRetrofit

        downloadButton.setOnClickListener {
            if (!(radioGlide.isChecked || radioLoadApp.isChecked || radioRetrofit.isChecked)) {
                Toast.makeText(this, "Please select a file to download", Toast.LENGTH_SHORT).show()
            } else if (downloadButton.buttonState != ButtonState.Loading) {
                val notificationManager =
                    ContextCompat.getSystemService(
                        this,
                        NotificationManager::class.java
                    ) as NotificationManager
                notificationManager.cancelNotifications()
                val url = when {
                    radioGlide.isChecked -> "https://github.com/bumptech/glide"
                    radioLoadApp.isChecked -> "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                    radioRetrofit.isChecked -> "https://github.com/square/retrofit"
                    else -> "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                }
                download(url)
                downloadButton.startDownloadAnimation()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(downloadReceiver)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "FileDownloader Download Complete"

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }

    companion object {

    }
}

fun NotificationManager.sendNotification(
    title: String,
    messageBody: String,
    applicationContext: Context
) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.file_downloader_channel_id)
    )
        .setSmallIcon(R.drawable.ic_filedownloader_notification_black)
        .setContentTitle(title)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}