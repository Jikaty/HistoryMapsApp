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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.historymapsapp.ui.navigation.ScreenType
import com.example.historymapsapp.ui.theme.BackgroundSepia
import com.example.historymapsapp.ui.theme.DarkBlue
import com.example.historymapsapp.ui.theme.TextDark
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider

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
    
    var userLocationLayer by remember { mutableStateOf<UserLocationLayer?>(null) }
    val placemarksStrongRefs = remember { mutableListOf<PlacemarkMapObject>() }

    val tapListener = remember {
        object : MapObjectTapListener {
            override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
                val index = mapObject.userData as? Int
                if (index != null) {
                    // Передаем ScreenType.MAP как источник
                    viewModel.setSelectedSight(index, ScreenType.MAP)
                    onNavigate(ScreenType.SIGHT_DETAILS)
                }
                return true
            }
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
                        layer.isVisible = true
                        layer.isHeadingEnabled = true
                        userLocationLayer = layer

                        map.move(
                            CameraPosition(Point(59.9414, 30.3141), 13.5f, 0f, 0f),
                            Animation(Animation.Type.SMOOTH, 0f),
                            null
                        )

                        map.mapObjects.addTapListener(tapListener)

                        placemarksStrongRefs.clear()
                        viewModel.sights.forEachIndexed { index, sight ->
                            val placemark = map.mapObjects.addPlacemark(sight.location)
                            placemark.apply {
                                setIcon(createNumberedMarker(index + 1))
                                userData = index
                                addTapListener(tapListener)
                            }
                            placemarksStrongRefs.add(placemark)
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { }
            )

            SmallFloatingActionButton(
                onClick = {
                    val target = userLocationLayer?.cameraPosition()?.target ?: state.userLocation
                    target?.let { point ->
                        mapView.map.move(
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
