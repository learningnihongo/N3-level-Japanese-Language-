package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.auth.AuthManager
import com.example.data.repository.FirestoreSyncRepository
import com.example.ui.MainApp
import com.example.ui.theme.KanjiKotobaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authManager = AuthManager(this)
        val syncRepository = FirestoreSyncRepository.create(this)

        setContent {
            KanjiKotobaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainApp(
                        authManager = authManager,
                        syncRepository = syncRepository
                    )
                }
            }
        }
    }
}
