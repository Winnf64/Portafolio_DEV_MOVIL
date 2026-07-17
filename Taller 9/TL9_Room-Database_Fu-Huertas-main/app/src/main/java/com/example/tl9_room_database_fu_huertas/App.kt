package com.example.tl9_room_database_fu_huertas

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.tl9_room_database_fu_huertas.ui.navigation.AppDestinations
import com.example.tl9_room_database_fu_huertas.ui.tasklist.TaskListScreen

@PreviewScreenSizes
@Composable
fun App_Gestion_de_TareasApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.TASKS) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestinations.TASKS -> TaskListScreen(modifier = Modifier.fillMaxSize())
        }
    }
}