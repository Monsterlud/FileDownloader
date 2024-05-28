package com.monsalud

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.monsalud.filedownloader.databinding.ActivityDetailBinding

enum class DownloadStatus {
    STATUS_PENDING,
    STATUS_RUNNING,
    STATUS_PAUSED,
    STATUS_SUCCESSFUL,
    STATUS_FAILED,
    STATUS_DEVICE_NOT_FOUND_ERROR,
    STATUS_INSUFFICIENT_SPACE_ERROR,
    STATUS_FILE_ERROR,
    STATUS_HTTP_DATA_ERROR,
    STATUS_TOO_MANY_REDIRECTS,
    STATUS_UNHANDLED_HTTP_CODE,
    STATUS_UNKNOWN_ERROR
}
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.contentDetail.btnBackToMainScreen.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val extras = intent?.extras
        val fileName = extras?.getString("fileName") ?: "fileName not found"
        val status = extras?.getInt("downloadStatus") ?: -1

        println("fileName: $fileName")
        println("downloadStatus: $status")

        binding.contentDetail.textFileNameContent.text = fileName
        binding.contentDetail.textStatusContent.text = when (status) {
            1 -> DownloadStatus.STATUS_PENDING.name
            2 -> DownloadStatus.STATUS_RUNNING.name
            4 -> DownloadStatus.STATUS_PAUSED.name
            8 -> DownloadStatus.STATUS_SUCCESSFUL.name
            16 -> DownloadStatus.STATUS_FAILED.name
            32 -> DownloadStatus.STATUS_DEVICE_NOT_FOUND_ERROR.name
            64 -> DownloadStatus.STATUS_INSUFFICIENT_SPACE_ERROR.name
            128 -> DownloadStatus.STATUS_FILE_ERROR.name
            256 -> DownloadStatus.STATUS_HTTP_DATA_ERROR.name
            512 -> DownloadStatus.STATUS_TOO_MANY_REDIRECTS.name
            1024 -> DownloadStatus.STATUS_UNHANDLED_HTTP_CODE.name
            else -> DownloadStatus.STATUS_UNKNOWN_ERROR.name
        }
    }
}
