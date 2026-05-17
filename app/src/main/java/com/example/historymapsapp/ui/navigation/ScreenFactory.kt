package com.example.historymapsapp.ui.navigation

import androidx.compose.runtime.Composable
import com.example.historymapsapp.ui.screens.MapScreen
import com.example.historymapsapp.ui.screens.ProfileScreen
import com.example.historymapsapp.ui.screens.RoutesScreen
import com.example.historymapsapp.ui.screens.StartScreen

enum class ScreenType { START, ROUTES, MAP, PROFILE }

interface Screen {
    @Composable
    fun Content()
}

class StartDestination(private val onNavigate: (ScreenType) -> Unit) : Screen {
    @Composable
    override fun Content() = StartScreen(onNavigate = { onNavigate(ScreenType.ROUTES) })
}

class RoutesDestination(private val onNavigate: (ScreenType) -> Unit) : Screen {
    @Composable
    override fun Content() = RoutesScreen(onNavigate = onNavigate)
}

class MapDestination(private val onNavigate: (ScreenType) -> Unit) : Screen {
    @Composable
    override fun Content() = MapScreen(onNavigate = onNavigate)
}

class ProfileDestination(private val onNavigate: (ScreenType) -> Unit) : Screen {
    @Composable
    override fun Content() = ProfileScreen(onNavigate = onNavigate)
}

class ScreenFactory(private val onNavigate: (ScreenType) -> Unit) {
    fun createScreen(type: ScreenType): Screen {
        return when (type) {
            ScreenType.START -> StartDestination(onNavigate)
            ScreenType.ROUTES -> RoutesDestination(onNavigate)
            ScreenType.MAP -> MapDestination(onNavigate)
            ScreenType.PROFILE -> ProfileDestination(onNavigate)
        }
    }
}
