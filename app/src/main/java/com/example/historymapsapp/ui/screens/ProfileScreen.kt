package com.example.historymapsapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.historymapsapp.R
import com.example.historymapsapp.ui.navigation.ScreenType
import com.example.historymapsapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigate: (ScreenType) -> Unit) {
    Scaffold(
        containerColor = BackgroundSepia,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Мой профиль",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontSize = 22.sp
                        ),
                        color = TextDark
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundSepia)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = BackgroundSepia, tonalElevation = 0.dp) {
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate(ScreenType.ROUTES) },
                    icon = { Icon(Icons.Outlined.Home, null) },
                    label = { Text("Главная") }
                )
                NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Outlined.Place, null) }, label = { Text("Карта") })
                NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Outlined.List, null) }, label = { Text("Таймлайн") })
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Outlined.Person, null) },
                    label = { Text("Профиль") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBlue,
                        selectedTextColor = DarkBlue,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Блок пользователя
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f))
                            .border(BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Person, null, modifier = Modifier.size(40.dp), tint = DarkBlue)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Исследователь\nПетербурга",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = TextDark,
                            lineHeight = 24.sp
                        )
                        Text("Уровень 3", fontSize = 14.sp, color = TextDark.copy(alpha = 0.6f))
                    }
                }
            }

            // Достижения
            item {
                Text(
                    "Мои достижения", 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.padding(bottom = 12.dp, top = 8.dp), 
                    color = TextDark
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AchievementCard("3", "Маршрута\nпройдено", Modifier.weight(1f))
                    AchievementCard("27", "Точек\nпосещено", Modifier.weight(1f))
                    AchievementCard("6", "Часов\nв пути", Modifier.weight(1f))
                }
            }

            // Недавние маршруты
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "Недавние маршруты", 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.padding(bottom = 12.dp), 
                    color = TextDark
                )
            }

            items(recentRoutesList) { route ->
                RecentRouteItem(route)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun AchievementCard(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(
                label, 
                fontSize = 11.sp, 
                textAlign = TextAlign.Center, 
                lineHeight = 14.sp, 
                color = TextDark.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun RecentRouteItem(route: RecentRoute) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp), 
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(route.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(route.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("Пройден ${route.date}", fontSize = 12.sp, color = TextDark.copy(alpha = 0.6f))
            }
        }
    }
}

data class RecentRoute(val title: String, val date: String, val imageRes: Int)

val recentRoutesList = listOf(
    RecentRoute("Петровский Петербург:\nокно в Европу", "12.05.2024", R.drawable.route_peter),
    RecentRoute("Столица Великих реформ", "08.05.2024", R.drawable.route_reforms)
)
