package com.example.historymapsapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.historymapsapp.ui.navigation.ScreenType
import com.example.historymapsapp.ui.theme.BackgroundSepia
import com.example.historymapsapp.ui.theme.DarkBlue
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.mapkit.directions.driving.DrivingRouterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigate: (ScreenType) -> Unit,
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }
    val map = remember { mapView.mapWindow.map }

    var userLocationLayer by remember { mutableStateOf<UserLocationLayer?>(null) }

    // Храним геометрию полученного маршрута, чтобы AndroidView мог её нарисовать
    var currentRouteGeometry by remember { mutableStateOf<Polyline?>(null) }

    // Инициализируем автомобильный роутер (DirectionsFactory)
    val drivingRouter = remember { DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.ONLINE)}
    var drivingSession by remember { mutableStateOf<DrivingSession?>(null) }

    val tapListener = remember {
        MapObjectTapListener { mapObject, _ ->
            val index = mapObject.userData as? Int
            if (index != null) {
                viewModel.setSelectedSight(index, ScreenType.MAP)
                onNavigate(ScreenType.SIGHT_DETAILS)
            }
            true
        }
    }

    // Слушаем изменения активного маршрута из ViewModel
    LaunchedEffect(state.activeRoutePoints) {
        val points = state.activeRoutePoints
        android.util.Log.d("MapScreen", "activeRoutePoints изменился: ${points?.size} точек")

        if (points != null && points.size >= 2) {
            val requestPoints = points.map {
                RequestPoint(it, RequestPointType.WAYPOINT, null, null)
            }

            drivingSession?.cancel() // Отменяем старый запрос, если он выполнялся

            drivingSession = drivingRouter.requestRoutes(
                requestPoints,
                DrivingOptions(),
                VehicleOptions(),
                object : DrivingSession.DrivingRouteListener {
                    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
                        android.util.Log.d("MapScreen", "Яндекс вернул ${routes.size} авто-маршрутов")
                        if (routes.isNotEmpty()) {
                            // Сохраняем геометрию первого (самого оптимального) маршрута
                            currentRouteGeometry = routes[0].geometry

                            // Фокусируем камеру на начало пути
                            map.move(
                                CameraPosition(points[0], 14f, 0f, 0f),
                                Animation(Animation.Type.SMOOTH, 0.8f),
                                null
                            )
                        }
                    }

                    override fun onDrivingRoutesError(error: Error) {
                        android.util.Log.e("MapScreen", "Ошибка авто-роутера: ${error.javaClass.simpleName}")
                    }
                }
            )
        } else {
            // Если маршрут сбросили
            drivingSession?.cancel()
            currentRouteGeometry = null
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    MapKitFactory.getInstance().onStart()
                    mapView.onStart()
                }
                Lifecycle.Event.ON_STOP -> {
                    mapView.onStop()
                    MapKitFactory.getInstance().onStop()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
            drivingSession?.cancel()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            viewModel.startTracking(fusedLocationClient)
            userLocationLayer?.isVisible = true
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            viewModel.startTracking(fusedLocationClient)
            userLocationLayer?.isVisible = true
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = BackgroundSepia, tonalElevation = 0.dp) {
                NavigationBarItem(selected = false, onClick = { onNavigate(ScreenType.ROUTES) }, icon = { Icon(Icons.Outlined.Home, null) }, label = { Text("Главная") })
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Place, null) },
                    label = { Text("Карта") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = DarkBlue, indicatorColor = Color.Transparent, selectedTextColor = DarkBlue)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate(ScreenType.TIMELINE) },
                    icon = { Icon(Icons.AutoMirrored.Outlined.List, null) },
                    label = { Text("Таймлайн") }
                )
                NavigationBarItem(selected = false, onClick = { onNavigate(ScreenType.PROFILE) }, icon = { Icon(Icons.Outlined.Person, null) }, label = { Text("Профиль") } )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            AndroidView(
                factory = {
                    mapView.apply {
                        map.setMapStyle(RETRO_MAP_STYLE)

                        val layer = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
                        layer.isVisible = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        layer.isHeadingEnabled = true
                        userLocationLayer = layer

                        map.move(
                            CameraPosition(Point(59.9414, 30.3141), 13.5f, 0f, 0f),
                            Animation(Animation.Type.SMOOTH, 0f),
                            null
                        )
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    val currentMap = view.mapWindow.map

                    // Полностью очищаем карту перед рендером нового кадра данных
                    currentMap.mapObjects.clear()
                    currentMap.mapObjects.addTapListener(tapListener)

                    // 1. Отрисовка геометрии автомобильного маршрута, если она есть
                    currentRouteGeometry?.let { geometry ->
                        currentMap.mapObjects.addPolyline(geometry).apply {
                            strokeWidth = 5f
                            setStrokeColor(android.graphics.Color.parseColor("#3498db"))
                            outlineWidth = 1.5f
                            outlineColor = android.graphics.Color.WHITE
                        }
                        android.util.Log.d("MapScreen", "Линия авто-маршрута добавлена на карту")
                    }

                    // 2. Отрисовка всех достопримечательностей
                    viewModel.sights.forEachIndexed { index, sight ->
                        currentMap.mapObjects.addPlacemark(sight.location).apply {
                            setIcon(createNumberedMarker(index + 1))
                            userData = index
                            addTapListener(tapListener)
                        }
                    }
                }
            )

            if (state.activeRoutePoints != null) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Маршрут активен", color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { viewModel.clearRoute() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Сбросить", tint = Color.Red)
                        }
                    }
                }
            }

            SmallFloatingActionButton(
                onClick = {
                    val target = userLocationLayer?.cameraPosition()?.target ?: state.userLocation
                    target?.let { point ->
                        map.move(
                            CameraPosition(point, 16f, 0f, 0f),
                            Animation(Animation.Type.SMOOTH, 0.8f),
                            null
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 16.dp, end = 16.dp)
                    .size(52.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .zIndex(1f),
                containerColor = DarkBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = "Моё положение",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun createNumberedMarker(number: Int): ImageProvider {
    val size = 80
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = android.graphics.Color.parseColor("#2c3e50")
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2.2f, paint)
    paint.color = android.graphics.Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 4f
    canvas.drawCircle(size / 2f, size / 2f, size / 2.2f, paint)
    paint.style = Paint.Style.FILL
    paint.textSize = 36f
    paint.textAlign = Paint.Align.CENTER
    val xPos = canvas.width / 2f
    val yPos = (canvas.height / 2f - (paint.descent() + paint.ascent()) / 2f)
    canvas.drawText(number.toString(), xPos, yPos, paint)
    return ImageProvider.fromBitmap(bitmap)
}

private const val RETRO_MAP_STYLE = """
[
  { "tags": { "all": ["land"] }, "stylers": { "color": "#f5eedc" } },
  { "tags": { "all": ["water"] }, "stylers": { "color": "#a2b9bc" } },
  { "tags": { "all": ["road"] }, "stylers": { "color": "#d1c4ae" } },
  { "tags": { "all": ["poi"] }, "stylers": { "visibility": "off" } }
]
"""