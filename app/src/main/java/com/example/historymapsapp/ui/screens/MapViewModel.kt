package com.example.historymapsapp.ui.screens

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.yandex.mapkit.geometry.Point

data class MapState(
    val userLocation: Point? = null,
    val totalDistance: Float = 0f,
    val currentSightIndex: Int = 0
)

data class Sight(
    val name: String,
    val location: Point,
    val imageRes: Int,
    val distanceToNext: String = ""
)

class MapViewModel : ViewModel() {
    private val _state = mutableStateOf(MapState())
    val state: State<MapState> = _state

    private var lastLocation: Location? = null

    // Список достопримечательностей (замени 0 на реальные R.drawable.id)
    val sights = listOf(
        Sight("Эрмитаж", Point(59.9398, 30.3146), 0),
        Sight("Исаакиевский собор", Point(59.9341, 30.3061), 0)
    )

    val routePoints = sights.map { it.location }

    @SuppressLint("MissingPermission")
    fun startTracking(fusedLocationClient: FusedLocationProviderClient) {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateDistanceMeters(1f)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                request, 
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.lastLocation?.let { location ->
                            val newPoint = Point(location.latitude, location.longitude)
                            
                            var addedDistance = 0f
                            lastLocation?.let {
                                addedDistance = it.distanceTo(location)
                            }
                            
                            lastLocation = location
                            _state.value = _state.value.copy(
                                userLocation = newPoint,
                                totalDistance = _state.value.totalDistance + addedDistance
                            )
                        }
                    }
                }, 
                Looper.getMainLooper() // КРИТИЧНО: указываем главный поток
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFormattedDistance(): String {
        val dist = _state.value.totalDistance
        return if (dist < 1000) {
            "${dist.toInt()} м"
        } else {
            "%.1f км".format(dist / 1000)
        }
    }
}
