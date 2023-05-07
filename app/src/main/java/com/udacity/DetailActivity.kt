package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val okButton: Button = findViewById(R.id.ok_button)
        okButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val fileNameTextView: TextView = findViewById(R.id.file_name)
        fileNameTextView.text = intent.getStringExtra("fileName")

        val statusTextView: TextView = findViewById(R.id.status_text)
        statusTextView.text = intent.getStringExtra("status")
        statusTextView.setTextColor(
            if (intent.getStringExtra("status") == "Success") Color.GREEN else Color.RED)

        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotifications()
    }

}
