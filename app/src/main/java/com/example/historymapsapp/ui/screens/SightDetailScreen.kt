package com.example.historymapsapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.historymapsapp.model.Sight
import com.example.historymapsapp.ui.navigation.ScreenType
import com.example.historymapsapp.ui.theme.BackgroundSepia
import com.example.historymapsapp.ui.theme.DarkBlue
import com.example.historymapsapp.ui.theme.TextDark

@Composable
fun SightDetailScreen(
    initialIndex: Int,
    viewModel: MapViewModel,
    onNavigate: (ScreenType) -> Unit
) {
    val sights = viewModel.sights
    // Состояние пейджера для реализации листания (свайпов)
    val pagerState = rememberPagerState(initialPage = initialIndex) { sights.size }

    Scaffold(
        containerColor = BackgroundSepia
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            val sight = sights[page]
            SightContent(
                sight = sight,
                current = page + 1,
                total = sights.size,
                onBack = { onNavigate(ScreenType.MAP) }
            )
        }
    }
}

@Composable
fun SightContent(sight: Sight, current: Int, total: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Верхний блок: Картинка и кнопка Назад
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            if (sight.imageRes != 0) {
                Image(
                    painter = painterResource(id = sight.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Заглушка, пока не подгружены фотографии
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray)
                )
            }

            // Кнопка Назад поверх фото
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = Color.White)
                }
            }

            // Индикатор страниц (например, "2 из 32")
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                color = DarkBlue.copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$current из $total",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Текстовый контент
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = sight.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = sight.era,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Основное описание
            Text(
                text = sight.description.ifEmpty { "Описание достопримечательности скоро будет добавлено." },
                fontSize = 16.sp,
                color = TextDark,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(24.dp))

            // Блок реформы
            Text(
                text = "Как реформа изменила это место?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextDark
            )
            Text(
                text = sight.reformHistory.ifEmpty { "История изменений этого места в эпоху реформ Петра I." },
                fontSize = 16.sp,
                color = TextDark,
                modifier = Modifier.padding(top = 12.dp),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Карточка "Интересный факт"
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFEBC06D),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = sight.interestingFact.ifEmpty { "Знаете ли вы, что это место хранит множество тайн?" },
                        fontSize = 14.sp,
                        color = TextDark,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка Аудиогид
            Button(
                onClick = { /* Запуск аудиогида */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Headset, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Аудиогид", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
