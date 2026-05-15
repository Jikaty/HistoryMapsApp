package com.example.historymapsapp
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.historymapsapp.ui.navigation.AppNavigation
import com.example.historymapsapp.ui.theme.HistoryMapsAppTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        enableEdgeToEdge()
        setContent {
            HistoryMapsAppTheme {
                AppNavigation(sharedPref)
            }
        }
    }
}
