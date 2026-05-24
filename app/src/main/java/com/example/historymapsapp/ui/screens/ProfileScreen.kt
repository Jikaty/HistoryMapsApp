package com.example.historymapsapp.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.historymapsapp.ui.navigation.ScreenType
import com.example.historymapsapp.ui.theme.*
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigate: (ScreenType) -> Unit,
    viewModel: MapViewModel
) {
    val state by viewModel.state
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    var sdk by remember { mutableStateOf<YandexAuthSdk?>(null) }

    var isSdkLoading by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                sdk = YandexAuthSdk.create(YandexAuthOptions(context.applicationContext))
            } catch (e: Exception) {
                Log.e("YandexAuth", "Ошибка инициализации SDK: ${e.message}")
            } finally {
                isSdkLoading = false
            }
        }
    }


    val launcher = sdk?.let { readySdk ->
        rememberLauncherForActivityResult(readySdk.contract) { result ->
            when (result) {
                is YandexAuthResult.Success -> {
                    val tokenValue = result.token.value

                    scope.launch(Dispatchers.IO) {
                        try {
                            val url = URL("https://login.yandex.ru/info?format=json")
                            val connection = url.openConnection() as HttpURLConnection
                            connection.setRequestProperty("Authorization", "OAuth $tokenValue")
                            connection.connectTimeout = 5000
                            connection.readTimeout = 5000

                            if (connection.responseCode == 200) {
                                val response = connection.inputStream.bufferedReader().use { it.readText() }
                                val json = JSONObject(response)

                                val firstName = json.optString("first_name", "")
                                val lastName = json.optString("last_name", "")
                                val displayName = json.optString("display_name", "Пользователь Яндекс")

                                val fullName = "$firstName $lastName".trim()
                                val finalName = if (fullName.isNotEmpty()) fullName else displayName

                                withContext(Dispatchers.Main) {
                                    viewModel.loginSuccess(finalName)
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Не удалось получить профиль", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("YandexAuth", "Ошибка сети при запросе профиля: ${e.message}")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Ошибка сети при загрузке профиля", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                is YandexAuthResult.Failure -> {
                    Log.e("YandexAuth", "Ошибка авторизации: ${result.exception.message}")
                    Toast.makeText(context, "Вход не удался", Toast.LENGTH_SHORT).show()
                }
                YandexAuthResult.Cancelled -> {
                    Log.d("YandexAuth", "Вход отменен пользователем")
                }
            }
        }
    }

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
                    if (state.isLoggedIn) {
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Выйти",
                                tint = TextDark
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundSepia)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = BackgroundSepia, tonalElevation = 0.dp) {
                NavigationBarItem(selected = false, onClick = { onNavigate(ScreenType.ROUTES) }, icon = { Icon(Icons.Outlined.Home, null) }, label = { Text("Главная") })
                NavigationBarItem(selected = false, onClick = { onNavigate(ScreenType.MAP) }, icon = { Icon(Icons.Outlined.Place, null) }, label = { Text("Карта") })
                NavigationBarItem(selected = false, onClick = { onNavigate(ScreenType.TIMELINE) }, icon = { Icon(Icons.Outlined.List, null) }, label = { Text("Таймлайн") })
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Outlined.Person, null) },
                    label = { Text("Профиль") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = DarkBlue, selectedTextColor = DarkBlue, indicatorColor = Color.Transparent)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isSdkLoading) {

                CircularProgressIndicator(color = DarkBlue)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!state.isLoggedIn) {
                        item {
                            Spacer(modifier = Modifier.height(60.dp))
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.5f))
                                    .border(BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Person, null, modifier = Modifier.size(50.dp), tint = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                "Войдите, чтобы подтянуть имя\nиз вашего Яндекс аккаунта",
                                textAlign = TextAlign.Center,
                                color = TextDark.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Serif
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = {
                                    if (launcher != null) {
                                        launcher.launch(YandexAuthLoginOptions())
                                    } else {
                                        Toast.makeText(context, "Ошибка: Яндекс SDK не готов", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00)),
                                shape = RoundedCornerShape(28.dp)
                            ) {
                                Text("Войти через Яндекс", fontSize = 16.sp, color = Color.Black)
                            }
                        }
                    } else {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
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
                                        text = state.userName ?: "Путешественник",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Serif,
                                        color = TextDark
                                    )
                                    Text("Уровень 1", fontSize = 14.sp, color = TextDark.copy(alpha = 0.6f))
                                }
                            }
                        }

                        item {
                            Text(
                                "Мои достижения",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp, top = 8.dp),
                                color = TextDark
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                              ) {
                                AchievementCard("0", "Маршрута", Modifier.weight(1f))
                                AchievementCard("0", "Точек", Modifier.weight(1f))
                                AchievementCard("0", "Часов", Modifier.weight(1f))
                            }
                        }
                    }
                }
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
