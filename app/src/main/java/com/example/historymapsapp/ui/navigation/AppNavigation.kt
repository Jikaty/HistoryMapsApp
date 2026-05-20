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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.historymapsapp.ui.screens.MapViewModel
import com.example.historymapsapp.ui.theme.BackgroundSepia

@Composable
fun AppNavigation(sharedPref: SharedPreferences) {
    val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)

    var currentScreenType by remember {
        mutableStateOf(if (isFirstLaunch) ScreenType.START else ScreenType.ROUTES)
    }

    val onNavigate: (ScreenType) -> Unit = { screenType ->
        if (currentScreenType == ScreenType.START && screenType == ScreenType.ROUTES) {
            sharedPref.edit().putBoolean("isFirstLaunch", false).apply()
        }
        currentScreenType = screenType
    }

    // ✅ Создаём ViewModel один раз и переиспользуем её
    val mapViewModel: MapViewModel = viewModel()

    val factory = remember(onNavigate, mapViewModel) {
        ScreenFactory(onNavigate = onNavigate, mapViewModel = mapViewModel)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundSepia
    ) {
        factory.createScreen(currentScreenType).Content()
    }
}