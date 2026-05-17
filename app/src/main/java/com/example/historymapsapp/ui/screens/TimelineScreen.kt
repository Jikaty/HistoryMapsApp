package com.example.historymapsapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.historymapsapp.model.Sight
import com.example.historymapsapp.ui.navigation.ScreenType
import com.example.historymapsapp.ui.theme.BackgroundSepia
import com.example.historymapsapp.ui.theme.DarkBlue
import com.example.historymapsapp.ui.theme.TextDark

@Composable
fun TimelineScreen(
    onNavigate: (ScreenType) -> Unit,
    viewModel: MapViewModel
) {
    val years = listOf(1700, 1750, 1800, 1850, 1900, 1950, 2000)
    var selectedYear by remember { mutableStateOf(1850) }
    
    // Состояние для управления прокруткой списка
    val listState = rememberLazyListState()

    // Сбрасываем прокрутку в начало при изменении выбранного года
    LaunchedEffect(selectedYear) {
        listState.scrollToItem(0)
    }

    // --- ЛОГИКА ФИЛЬТРАЦИИ ---
    // Выбираем достопримечательности, год которых попадает в текущий 50-летний интервал
    val filteredSights = viewModel.sights.filter { sight ->
        sight.year >= selectedYear && sight.year < selectedYear + 50
    }

    Scaffold(
        containerColor = BackgroundSepia,
        bottomBar = {
            TimelineBottomBar(onNavigate)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- ФИКСИРОВАННАЯ ВЕРХНЯЯ ЧАСТЬ (Заголовок и Линейка) ---
            Surface(
                color = BackgroundSepia,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Таймлайн",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TimelineYearSelector(
                        years = years,
                        selectedYear = selectedYear,
                        onYearSelected = { selectedYear = it }
                    )
                }
            }

            // --- ПРОКРУЧИВАЕМАЯ ЧАСТЬ (Описание эпохи и Карточки) ---
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    val period = getEraInfo(selectedYear)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = period.years,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = period.title,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark,
                            lineHeight = 34.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = period.description,
                            fontSize = 15.sp,
                            color = TextDark.copy(alpha = 0.8f),
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(
                            text = "Достопримечательности периода",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (filteredSights.isEmpty()) {
                            Text(
                                text = "В этот период значимых объектов пока не найдено.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }
                }

                // Список ОТФИЛЬТРОВАННЫХ карточек
                itemsIndexed(filteredSights) { _, sight ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
                        SightTimelineCard(
                            sight = sight,
                            onClick = {
                                // Находим оригинальный индекс объекта для корректной работы Pager
                                val originalIndex = viewModel.sights.indexOf(sight)
                                viewModel.setSelectedSight(originalIndex, ScreenType.TIMELINE)
                                onNavigate(ScreenType.SIGHT_DETAILS)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineYearSelector(
    years: List<Int>,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            years.forEach { year ->
                val isSelected = year == selectedYear
                Text(
                    text = year.toString(),
                    fontSize = 12.sp,
                    color = if (isSelected) TextDark else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .width(40.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onYearSelected(year) },
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            HorizontalDivider(
                color = Color(0xFFD4C0A1),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                years.forEach { year ->
                    val isSelected = year == selectedYear
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 20.dp else 10.dp)
                            .background(
                                color = if (isSelected) Color(0xFFA68B5C) else Color(0xFFD4C0A1),
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onYearSelected(year) }
                            .padding(if (isSelected) 4.dp else 0.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(modifier = Modifier.size(10.dp).background(Color.White, CircleShape))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SightTimelineCard(sight: Sight, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (sight.imageRes != 0) {
                Image(
                    painter = painterResource(id = sight.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = sight.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Год постройки: ${sight.year} · Подробнее →",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun TimelineBottomBar(onNavigate: (ScreenType) -> Unit) {
    NavigationBar(containerColor = BackgroundSepia, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = false,
            onClick = { onNavigate(ScreenType.ROUTES) },
            icon = { Icon(Icons.Outlined.Home, null) },
            label = { Text("Главная") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { onNavigate(ScreenType.MAP) },
            icon = { Icon(Icons.Outlined.Place, null) },
            label = { Text("Карта") }
        )
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Outlined.List, null) },
            label = { Text("Таймлайн") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkBlue,
                selectedTextColor = DarkBlue,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { onNavigate(ScreenType.PROFILE) },
            icon = { Icon(Icons.Outlined.Person, null) },
            label = { Text("Профиль") }
        )
    }
}

private data class EraInfo(val years: String, val title: String, val description: String)

private fun getEraInfo(year: Int): EraInfo {
    return when(year) {
        1700 -> EraInfo("1703–1725", "Эпоха Петра I", "Основание города, первые крепости и выход к Балтийскому морю. Время коренных преобразований.")
        1850 -> EraInfo("1801–1855", "Эпоха Великих реформ", "Период масштабных преобразований в управлении, экономике и городской среде.")
        1900 -> EraInfo("1894–1917", "Серебряный век", "Расцвет культуры, искусства и архитектуры модерна. Время перемен.")
        else -> EraInfo("$year–${year+50}", "Исторический период", "В этот период город продолжал активно развиваться и приобретать новые черты.")
    }
}
