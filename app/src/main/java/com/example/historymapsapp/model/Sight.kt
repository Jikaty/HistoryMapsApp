package com.example.historymapsapp.model

import com.yandex.mapkit.geometry.Point

data class Sight(
    val name: String,
    val location: Point,
    val year: Int = 1700,
    val era: String = "Петровская эпоха, начало XVIII века",
    val description: String = "",
    val reformHistory: String = "",
    val interestingFact: String = "",
    val imageRes: Int = 0
)
