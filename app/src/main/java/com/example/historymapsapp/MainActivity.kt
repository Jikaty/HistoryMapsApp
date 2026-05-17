package com.example.historymapsapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.historymapsapp.ui.navigation.AppNavigation
import com.example.historymapsapp.ui.theme.HistoryMapsAppTheme
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем ключ ПЕРВЫМ делом, даже до super.onCreate
        MapKitFactory.setApiKey("ac475df4-01de-4573-83eb-61e565e78d17")
        
        super.onCreate(savedInstanceState)
        
        // Инициализируем MapKit
        MapKitFactory.initialize(this)
        
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        enableEdgeToEdge()
        setContent {
            HistoryMapsAppTheme {
                AppNavigation(sharedPref)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}
