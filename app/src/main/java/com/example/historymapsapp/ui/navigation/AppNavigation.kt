package com.example.historymapsapp.ui.navigation

import android.content.SharedPreferences
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.historymapsapp.ui.theme.BackgroundSepia

@Composable
fun AppNavigation(sharedPref: SharedPreferences) {
    val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)

    // Состояние экрана
    var currentScreenType by remember {
        mutableStateOf(if (isFirstLaunch) ScreenType.START else ScreenType.ROUTES)
    }

    // Фабрика
    val factory = remember {
        ScreenFactory(onNavigate = {
            sharedPref.edit().putBoolean("isFirstLaunch", false).apply()
            currentScreenType = ScreenType.ROUTES
        })
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundSepia
    ) {
        factory.createScreen(currentScreenType).Content()
    }
}