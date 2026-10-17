package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ui.MainApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            com.google.firebase.FirebaseApp.initializeApp(applicationContext)
        } catch (e: Throwable) {
            android.util.Log.w("MainActivity", "FirebaseApp init fallback: ${e.message}")
        }
        enableEdgeToEdge()

        setContent {
            MainApp()
        }
    }
}
