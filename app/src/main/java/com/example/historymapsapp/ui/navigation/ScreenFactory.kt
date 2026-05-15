package com.example.historymapsapp.ui.navigation

import androidx.compose.runtime.Composable
import com.example.historymapsapp.ui.screens.RoutesScreen
import com.example.historymapsapp.ui.screens.StartScreen

enum class ScreenType { START, ROUTES }

interface Screen {
    @Composable
    fun Content()
}

class StartDestination(private val onNavigate: () -> Unit) : Screen {
    @Composable
    override fun Content() = StartScreen(onNavigate)
}

class RoutesDestination : Screen {
    @Composable
    override fun Content() = RoutesScreen()
}

class ScreenFactory(private val onNavigate: () -> Unit) {
    fun createScreen(type: ScreenType): Screen {
        return when (type) {
            ScreenType.START -> StartDestination(onNavigate)
            ScreenType.ROUTES -> RoutesDestination()
        }
    }
}
