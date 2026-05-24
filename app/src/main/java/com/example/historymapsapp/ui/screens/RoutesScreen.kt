package com.example.historymapsapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.historymapsapp.data.RouteRepository
import com.example.historymapsapp.model.Route
import com.example.historymapsapp.ui.navigation.ScreenType
import com.example.historymapsapp.ui.theme.BackgroundSepia
import com.example.historymapsapp.ui.theme.DarkBlue
import com.example.historymapsapp.ui.theme.TextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesScreen(
    onNavigate: (ScreenType) -> Unit,
    viewModel: MapViewModel
) {
    val allRoutes = RouteRepository.getRoutes()
    var selectedEra by remember { mutableStateOf("XVIII век") }

    // Фильтруем маршруты в зависимости от выбранного чипа
    val filteredRoutes = allRoutes.filter { it.era == selectedEra }

    Scaffold(
        containerColor = BackgroundSepia,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Маршруты",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Normal,
                            fontSize = 22.sp
                        ),
                        color = TextDark
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundSepia
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BackgroundSepia,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text("Главная") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBlue,
                        selectedTextColor = DarkBlue,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate(ScreenType.MAP) },
                    icon = { Icon(Icons.Outlined.Place, null) },
                    label = { Text("Карта") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate(ScreenType.TIMELINE) },
                    icon = { Icon(Icons.Outlined.List, null) },
                    label = { Text("Таймлайн") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate(ScreenType.PROFILE) },
                    icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    label = { Text("Профиль") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val eras = listOf("XVIII век", "XIX век", "XX век")
                eras.forEach { era ->
                    item {
                        EraChip(
                            text = era,
                            isSelected = selectedEra == era,
                            onClick = { selectedEra = era }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (filteredRoutes.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Маршрутов для этого века пока нет", color = Color.Gray)
                        }
                    }
                } else {
                    itemsIndexed(filteredRoutes) { _, route ->
                        RouteCard(route) {
                            // Находим индекс в общем списке для ViewModel
                            val originalIndex = allRoutes.indexOf(route)
                            viewModel.selectRoute(originalIndex)
                            onNavigate(ScreenType.MAP)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EraChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) DarkBlue else Color.White.copy(alpha = 0.4f),
        shape = RoundedCornerShape(20.dp),
        border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            color = if (isSelected) Color.White else TextDark,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun RouteCard(route: Route, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = route.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = route.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("${route.distance} км", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                    Text("${route.points} точек", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                    Text("~ ${route.time}", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                }
            }
        }
    }
}
