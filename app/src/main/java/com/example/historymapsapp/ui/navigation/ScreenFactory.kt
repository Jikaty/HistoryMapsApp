package com.example.historymapsapp.ui.navigation

import androidx.compose.runtime.Composable
import com.example.historymapsapp.ui.screens.MapScreen
import com.example.historymapsapp.ui.screens.MapViewModel
import com.example.historymapsapp.ui.screens.ProfileScreen
import com.example.historymapsapp.ui.screens.RoutesScreen
import com.example.historymapsapp.ui.screens.SightDetailScreen
import com.example.historymapsapp.ui.screens.StartScreen
import com.example.historymapsapp.ui.screens.TimelineScreen

enum class ScreenType { START, ROUTES, MAP, PROFILE, SIGHT_DETAILS, TIMELINE }

interface Screen {
    @Composable
    fun Content()
}

class StartDestination(private val onNavigate: (ScreenType) -> Unit) : Screen {
    @Composable
    override fun Content() = StartScreen(onNavigate = { onNavigate(ScreenType.ROUTES) })
}

class RoutesDestination(
    private val onNavigate: (ScreenType) -> Unit,
    private val mapViewModel: MapViewModel
) : Screen {
    @Composable
    override fun Content() = RoutesScreen(onNavigate = onNavigate, viewModel = mapViewModel)
}

class MapDestination(
    private val onNavigate: (ScreenType) -> Unit,
    private val mapViewModel: MapViewModel
) : Screen {
    @Composable
    override fun Content() = MapScreen(onNavigate = onNavigate, viewModel = mapViewModel)
}

class SightDetailsDestination(
    private val onNavigate: (ScreenType) -> Unit,
    private val mapViewModel: MapViewModel
) : Screen {
    @Composable
    override fun Content() {
        val state = mapViewModel.state.value
        SightDetailScreen(
            initialIndex = state.selectedSightIndex ?: 0,
            viewModel = mapViewModel,
            onNavigate = onNavigate
        )
    }
}

class ProfileDestination(
    private val onNavigate: (ScreenType) -> Unit,
    private val mapViewModel: MapViewModel
) : Screen {
    @Composable
    override fun Content() = ProfileScreen(onNavigate = onNavigate, viewModel = mapViewModel)
}

class TimelineDestination(
    private val onNavigate: (ScreenType) -> Unit,
    private val mapViewModel: MapViewModel
) : Screen {
    @Composable
    override fun Content() = TimelineScreen(onNavigate = onNavigate, viewModel = mapViewModel)
}

class ScreenFactory(
    private val onNavigate: (ScreenType) -> Unit,
    private val mapViewModel: MapViewModel
) {

    @Composable
    fun createScreen(type: ScreenType): Screen {
        return when (type) {
            ScreenType.START -> StartDestination(onNavigate)
            ScreenType.ROUTES -> RoutesDestination(onNavigate, mapViewModel)
            ScreenType.MAP -> MapDestination(onNavigate, mapViewModel)
            ScreenType.SIGHT_DETAILS -> SightDetailsDestination(onNavigate, mapViewModel)
            ScreenType.PROFILE -> ProfileDestination(onNavigate, mapViewModel)
            ScreenType.TIMELINE -> TimelineDestination(onNavigate, mapViewModel)
        }
    }
}
