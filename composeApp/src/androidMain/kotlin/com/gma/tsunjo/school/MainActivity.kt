package com.gma.tsunjo.school

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gma.tsunjo.school.auth.initAndroidSettings
import com.gma.tsunjo.school.firebase.FirebaseManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize Android settings before Koin starts
        initAndroidSettings(applicationContext)
        
        // Initialize Firebase
        FirebaseManager.initialize()

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}