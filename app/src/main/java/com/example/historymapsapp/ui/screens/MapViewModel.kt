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
            description = "Петропавловская крепость — первое сооружение Санкт-Петербурга, заложенное Петром I в 1703 году." +
                    "Именно отсюда началась история нового города, который должен был стать «окном в Европу»." +
                    "Крепость строилась как оборонительный объект для защиты от шведов во время Северной войны, однако со временем превратилась в политический и административный центр молодой столицы.",
            reformHistory = "Военная реформа Петра I превратила Заячий остров в стратегический центр новой столицы." +
                    "Крепость стала символом укрепления российской армии и выхода страны к Балтийскому морю. Вокруг неё начал формироваться будущий Санкт-Петербург.",
            interestingFact = "Ангел на шпиле собора является самым высоким флюгером в России.",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight(
            name = "Кунсткамера",
            location = Point(59.9414, 30.3047),
            description = "Кунсткамера — первый музей России, основанный Петром I в 1714 году. Царь собирал редкие предметы, научные инструменты и необычные экспонаты со всей Европы, стремясь развивать в стране науку и просвещение.",
            reformHistory = "Реформы Петра I в области науки и образования сделали Кунсткамеру первым публичным музеем России. Это место стало символом перехода страны к европейской культуре, науке и просвещению.",
            interestingFact = "Изначально вход в музей был бесплатным, а посетителей угощали чаркой водки.",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight(
            name = "Ростральные колонны",
            location = Point(59.9441, 30.3061),
            description = "Ростральные колонны были установлены на Стрелке Васильевского острова в начале XIX века и стали символом морской мощи Российской империи. Их украшения выполнены в виде носов кораблей — ростров, которые использовались как знак морских побед.\n" +
                    "Во времена Петра I развитие флота являлось одной из главных государственных задач. Петербург строился как морская столица России, а район Стрелки стал важным портовым и торговым центром города.",
            reformHistory = "Развитие флота и морской торговли превратило Стрелку Васильевского острова в важнейший портовый район Петербурга. Ростральные колонны стали архитектурным символом морской мощи Российской империи.",
            interestingFact = "В чашах на вершинах раньше зажигали конопляное масло.",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Биржа", Point(59.9436, 30.3061),
            description = "10. Биржа и Гостиный двор\n" +
                    "Биржа и торговые ряды на Васильевском острове стали центром экономической жизни Петербурга. Здесь велась международная торговля, заключались сделки и развивались коммерческие связи с Европой.\n" +
                    "Экономические реформы Петра I способствовали развитию торговли и промышленности. Петербург быстро превратился в крупнейший торговый порт Российской империи.",
            reformHistory = "Экономические реформы и развитие международной торговли сделали этот район центром коммерческой жизни Петербурга. Здесь формировались новые торговые связи России с Европой.",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Здание двенадцати коллегий", Point(59.9419, 30.2995),
            description = "Здание Двенадцати коллегий было построено для размещения новых органов государственной власти, созданных Петром I вместо старой приказной системы. Здесь находились коллегии — прообраз современных министерств.\n" +
                    "Реформа государственного управления изменила структуру власти в России, сделав её более централизованной и европейской по образцу западных стран. Архитектура здания отражает строгий и рациональный подход эпохи Петра.",
            reformHistory = "Административная реформа Петра I изменила систему управления государством. Здание стало центром новых органов власти, заменивших старую приказную систему, и символом модернизации государственного аппарата.",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Академия наук", Point(59.9392, 30.3025),
            description = "Академия наук была основана в 1724 году по инициативе Петра I. Её создание стало важным шагом в развитии науки, образования и инженерного дела в России.\n" +
                    "Пётр стремился превратить Петербург в интеллектуальный центр государства. В Академии работали выдающиеся европейские учёные, а позже именно здесь формировались основы российской научной школы.",
            reformHistory = "Реформы в сфере образования и науки сделали Петербург научным центром страны. Академия наук стала местом развития российской науки, инженерии и подготовки учёных нового поколения.",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Михайловский замок", Point(59.9401, 30.3385),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Казанский собор", Point(59.9344, 30.3245),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Зимний дворец", Point(59.9398, 30.3146),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Александровская колонна", Point(59.9390, 30.3158),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Арка Главного штаба", Point(59.9380, 30.3167),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Аничков мост", Point(59.9332, 30.3433),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Здание Государственного банка", Point(59.9351, 30.3298),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Александринский театр", Point(59.9317, 30.3362),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Троицкий мост", Point(59.9486, 30.3274),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Троице-Измайловский собор", Point(59.9165, 30.3061),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Русский музей", Point(59.9386, 30.3323),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Храм Спаса на Крови", Point(59.9401, 30.3289),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Елисеевский магазин", Point(59.9339, 30.3383),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Памятник Екатерине II", Point(59.9323, 30.3368),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Набережная Мойки", Point(59.9358, 30.3218),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Дом Пушкина", Point(59.9419, 30.3210),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Мариинский театр", Point(59.9258, 30.2961),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Дом писателей", Point(59.9478, 30.3486),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Николаевская морская академия", Point(59.9352, 30.2711),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Памятник Николаю I", Point(59.9328, 30.3082),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Большой зал Филармонии", Point(59.9363, 30.3313),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Адмиралтейство", Point(59.9375, 30.3085),
            description = "Адмиралтейство являлось главным кораблестроительным центром Российской империи. Здесь строились военные корабли для Балтийского флота, который Пётр I создавал практически с нуля.\n" +
                    "Развитие флота было ключевой задачей петровских реформ. Благодаря Адмиралтейству Петербург стал не только новой столицей, но и важнейшим морским центром страны.",
            reformHistory = "Военно-морская реформа Петра I превратила Адмиралтейство в главный центр кораблестроения России. Здесь создавался Балтийский флот, благодаря которому Россия укрепила своё положение в Европе.",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Летний дворец Петра I", Point(59.9467, 30.3370),
            description = "Летний дворец Петра I — одна из первых царских резиденций Петербурга. Небольшой и относительно скромный дворец отражает личные вкусы императора и его стремление к европейскому образу жизни.\n" +
                    "В отличие от роскошных дворцов последующих эпох, резиденция Петра подчёркивает практичность и рациональность, характерные для его реформ и образа правления.",
            reformHistory = "Европеизация России повлияла на архитектуру и образ жизни дворянства. Летний дворец стал примером нового стиля жизни, ориентированного на европейскую культуру и практичность.",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Исаакиевский собор", Point(59.9341, 30.3061),
            description = "Исаакиевский собор стал одним из крупнейших храмов Петербурга и символом имперского величия города. Хотя современное здание было завершено позже, история собора начинается ещё в петровскую эпоху.\n" +
                    "Собор отражает развитие архитектуры и инженерной мысли России, а также рост статуса Петербурга как столицы огромной империи.",
            reformHistory = "Рост Петербурга как столицы империи потребовал создания масштабных архитектурных символов. Собор стал отражением усиления политического и культурного значения города.",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Медный всадник", Point(59.9364, 30.3022),
            description = "Памятник Петру I, известный как Медный всадник, был установлен по приказу Екатерины II. Скульптура символизирует силу реформатора и роль Петра в создании нового государства.\n" +
                    "Монумент стал одним из главных символов Петербурга и всей петровской эпохи. Он подчёркивает идею движения России вперёд и её превращения в европейскую державу.",
            reformHistory = "Памятник закрепил образ Петра I как великого реформатора. Это место стало символом преобразований России, её модернизации и превращения в европейскую державу.",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        ),
        Sight("Гостиный двор", Point(59.9343, 30.3315),
            description = "",
            reformHistory = "",
            interestingFact = "",
            imageRes = R.drawable.petropavlovskaya_krepost
        )
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
