package com.example.historymapsapp.data

import com.example.historymapsapp.R
import com.example.historymapsapp.model.Route

object RouteRepository {
    fun getRoutes(): List<Route> {
        return listOf(
            Route(
                title = "Петровский Петербург:\nокно в Европу",
                distance = "2,5",
                points = 10,
                time = "2 ч",
                imageRes = R.drawable.route_peter
            ),
            Route(
                title = "Столица Великих реформ:\nбанки, вокзалы и доходные дома",
                distance = "3,1",
                points = 12,
                time = "2,5 ч",
                imageRes = R.drawable.route_reforms
            ),
            Route(
                title = "Индустриальный рывок:\nзаводы и рабочие окраины",
                distance = "4,0",
                points = 15,
                time = "3 ч",
                imageRes = R.drawable.route_industrial
            )
        )
    }
}
