package com.example.tl9_room_database_fu_huertas.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    TASKS("Tareas", Icons.Default.CheckCircle),
}