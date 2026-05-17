package com.example.historymapsapp.ui.screens

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.historymapsapp.model.Sight
import com.example.historymapsapp.R
import com.google.android.gms.location.*
import com.yandex.mapkit.geometry.Point

data class MapState(
    val userLocation: Point? = null,
    val totalDistance: Float = 0f,
    val selectedSightIndex: Int = 0 
)

class MapViewModel : ViewModel() {
    private val _state = mutableStateOf(MapState())
    val state: State<MapState> = _state

    private var lastLocation: Location? = null

    // Полный список 32 достопримечательностей с полями под твой текст
    val sights = listOf(
        Sight(
            name = "Петропавловская крепость",
            location = Point(59.9502, 30.3165),
            description = "Историческое ядро Санкт-Петербурга. Здесь Петр I заложил фундамент будущей империи.",
            reformHistory = "Строительство крепости ознаменовало переход к европейской фортификации и утверждение России на Балтике.",
            interestingFact = "Ангел на шпиле собора является самым высоким флюгером в России."
        ),
        Sight(
            name = "Кунсткамера",
            location = Point(59.9414, 30.3047),
            description = "Первый музей России. Символ стремления Петра I к науке, просвещению и открытости миру.",
            reformHistory = "Создание музея стало частью реформ по развитию образования и светской науки в России.",
            interestingFact = "Изначально вход в музей был бесплатным, а посетителей угощали чаркой водки."
        ),
        Sight(
            name = "Ростральные колонны",
            location = Point(59.9441, 30.3061),
            description = "Величественные колонны-маяки на стрелке Васильевского острова.",
            reformHistory = "Олицетворяли морскую мощь России и статус города как торгового порта.",
            interestingFact = "В чашах на вершинах раньше зажигали конопляное масло."
        ),
        Sight("Биржа", Point(59.9436, 30.3061), description = "Здание Биржи на стрелке Васильевского острова."),
        Sight("Здание двенадцати коллегий", Point(59.9419, 30.2995), description = "Главное здание университета."),
        Sight("Академия наук", Point(59.9392, 30.3025)),
        Sight("Михайловский замок", Point(59.9401, 30.3385)),
        Sight("Казанский собор", Point(59.9344, 30.3245)),
        Sight("Зимний дворец", Point(59.9398, 30.3146)),
        Sight("Александровская колонна", Point(59.9390, 30.3158)),
        Sight("Арка Главного штаба", Point(59.9380, 30.3167)),
        Sight("Аничков мост", Point(59.9332, 30.3433)),
        Sight("Здание Государственного банка", Point(59.9351, 30.3298)),
        Sight("Александринский театр", Point(59.9317, 30.3362)),
        Sight("Троицкий мост", Point(59.9486, 30.3274)),
        Sight("Троице-Измайловский собор", Point(59.9165, 30.3061)),
        Sight("Русский музей", Point(59.9386, 30.3323)),
        Sight("Храм Спаса на Крови", Point(59.9401, 30.3289)),
        Sight("Елисеевский магазин", Point(59.9339, 30.3383)),
        Sight("Памятник Екатерине II", Point(59.9323, 30.3368)),
        Sight("Набережная Мойки", Point(59.9358, 30.3218)),
        Sight("Дом Пушкина", Point(59.9419, 30.3210)),
        Sight("Мариинский театр", Point(59.9258, 30.2961)),
        Sight("Дом писателей", Point(59.9478, 30.3486)),
        Sight("Николаевская морская академия", Point(59.9352, 30.2711)),
        Sight("Памятник Николаю I", Point(59.9328, 30.3082)),
        Sight("Большой зал Филармонии", Point(59.9363, 30.3313)),
        Sight("Адмиралтейство", Point(59.9375, 30.3085)),
        Sight("Летний дворец Петра I", Point(59.9467, 30.3370)),
        Sight("Исаакиевский собор", Point(59.9341, 30.3061)),
        Sight("Медный всадник", Point(59.9364, 30.3022)),
        Sight("Гостиный двор", Point(59.9343, 30.3315))
    )

    fun setSelectedSight(index: Int) {
        _state.value = _state.value.copy(selectedSightIndex = index)
    }

    @SuppressLint("MissingPermission")
    fun startTracking(fusedLocationClient: FusedLocationProviderClient) {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()
        try {
            fusedLocationClient.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        val newPoint = Point(location.latitude, location.longitude)
                        val dist = lastLocation?.distanceTo(location) ?: 0f
                        lastLocation = location
                        _state.value = _state.value.copy(
                            userLocation = newPoint,
                            totalDistance = _state.value.totalDistance + dist
                        )
                    }
                }
            }, Looper.getMainLooper())
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun getFormattedDistance(): String {
        val dist = _state.value.totalDistance
        return if (dist < 1000) "${dist.toInt()} м" else "%.1f км".format(dist / 1000)
    }
}
