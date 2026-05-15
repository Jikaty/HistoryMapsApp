package com.example.historymapsapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.historymapsapp.ui.navigation.AppNavigation
import com.example.historymapsapp.ui.navigation.ScreenFactory
import com.example.historymapsapp.ui.navigation.ScreenType
import com.example.historymapsapp.ui.theme.BackgroundSepia
import com.example.historymapsapp.ui.theme.HistoryMapsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        enableEdgeToEdge()
        setContent {
            HistoryMapsAppTheme {
                AppNavigation(sharedPref)
            }
        }
    }
}
