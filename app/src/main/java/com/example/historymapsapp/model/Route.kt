package com.example.historymapsapp.model

import androidx.annotation.DrawableRes

data class Route(
    val title: String,
    val distance: String,
    val points: Int,
    val time: String,
    @DrawableRes val imageRes: Int,
    val sightIndices: List<Int> = emptyList() // Индексы достопримечательностей из MapViewModel.sights
)
