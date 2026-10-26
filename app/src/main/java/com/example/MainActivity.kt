package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.reminder.NotificationHelper
import com.example.reminder.ReminderScheduler
import com.example.reminder.SrsReminderScheduler
import com.example.ui.MainApp

class MainActivity : ComponentActivity() {

    private var destinationRoute by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            com.google.firebase.FirebaseApp.initializeApp(applicationContext)
        } catch (e: Throwable) {
            android.util.Log.w("MainActivity", "FirebaseApp init fallback: ${e.message}")
        }

        // Initialize Notification Channels and Background Schedulers
        NotificationHelper.createNotificationChannel(applicationContext)
        ReminderScheduler.rescheduleFromPreferences(applicationContext)
        SrsReminderScheduler.initScheduler(applicationContext)

        // Check if launched from a notification intent
        destinationRoute = intent?.getStringExtra("EXTRA_NAVIGATE_TO")

        enableEdgeToEdge()

        setContent {
            MainApp(initialDestination = destinationRoute)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val target = intent.getStringExtra("EXTRA_NAVIGATE_TO")
        if (!target.isNullOrBlank()) {
            destinationRoute = target
        }
    }
}
