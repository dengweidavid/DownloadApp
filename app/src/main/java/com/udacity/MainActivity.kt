package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    lateinit var loadingButton: LoadingButton

    private var downloadID: Long = 0
    private var downloadFileName = ""
    private var downloadUrl: String = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton = findViewById(R.id.loading_button)
        loadingButton.buttonState = ButtonState.Completed
        loadingButton.setOnClickListener {
            download(downloadUrl)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor =
                    downloadManager.query(id.let { DownloadManager.Query().setFilterById(it) })

                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        loadingButton.buttonState = ButtonState.Completed
                        context?.let {
                            notificationManager.sendNotification(it, downloadFileName, "Success")
                        }
                    } else {
                        loadingButton.buttonState = ButtonState.Completed
                        context?.let {
                            notificationManager.sendNotification(it, downloadFileName, "Failed")
                        }
                    }

                }
            }
        }
    }

    private fun download(url: String) {
        if (url.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_selection_warning), Toast.LENGTH_LONG).show()
            return
        }

        loadingButton.buttonState = ButtonState.Clicked

        notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        createChannel(getString(R.string.LoadApp_notification_channel_id), getString(R.string.LoadApp_notification_channel_name))

        val request =
            DownloadManager.Request(Uri.parse(url))
//                .setTitle(getString(R.string.app_name))
//                .setDescription(getString(R.string.app_description))
//                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        loadingButton.buttonState = ButtonState.Loading
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val isChecked = view.isChecked
            when (view.getId()) {
                R.id.glide_button ->
                    if (isChecked) {
                        downloadUrl = GLIDE_URL
                        downloadFileName = getString(R.string.glide_text)
                    }

                R.id.load_app_button ->
                    if (isChecked) {
                        downloadUrl = LOAD_APP_URL
                        downloadFileName = getString(R.string.load_app_text)
                    }

                R.id.retrofit_button -> {
                    if (isChecked) {
                        downloadUrl = RETROFIT_URL
                        downloadFileName = getString(R.string.retrofit_text)
                    }
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download is done!"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/heads/master.zip"

        private const val CHANNEL_ID = "channelId"
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
