package com.example.historymapsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.historymapsapp.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HistoryMapsAppTheme {
                StartScreen()
            }
        }
    }
}

@Composable
fun StartScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSepia)
    ) {
        // Фоновое изображение: теперь оно гарантированно заполняет ВЕСЬ экран.
        // ContentScale.Crop заполняет всё пространство, сохраняя пропорции, 
        // а Alignment.TopCenter удерживает верхнюю часть (здание) в поле зрения.
        Image(
            painter = painterResource(id = R.drawable.background_petersburg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Увеличиваем отступ сверху до 5.0, чтобы "вытолкнуть" текст вниз.
            // На макете заголовок находится в нижней трети экрана, где фон чистый и светлый.
            Spacer(modifier = Modifier.weight(5f))

            // Заголовок: Serif, изящный, не слишком жирный
            Text(
                text = "ПЕТЕРБУРГ\nСКВОЗЬ РЕФОРМЫ",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.2.sp
                ),
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Подзаголовок: аккуратный текст под заголовком
            Text(
                text = "История города через маршруты,\nархитектуру и перемены",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = TextDark.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            // Промежуток между текстом и кнопкой
            Spacer(modifier = Modifier.weight(1.5f))

            // Кнопка: темно-синяя, скругленная, как на примере
            Button(
                onClick = { /* TODO: Навигация */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                shape = RoundedCornerShape(32.dp)
            ) {
                Text(
                    text = "Начать исследование",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            
            // Маленький отступ от нижнего края
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StartScreenPreview() {
    HistoryMapsAppTheme {
        StartScreen()
    }
}
