package com.example.tl9_room_database_fu_huertas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tl9_room_database_fu_huertas.ui.theme.TL9_RoomDatabase_FuHuertasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TL9_RoomDatabase_FuHuertasTheme {
                App_Gestion_de_TareasApp()
            }
        }
    }
}