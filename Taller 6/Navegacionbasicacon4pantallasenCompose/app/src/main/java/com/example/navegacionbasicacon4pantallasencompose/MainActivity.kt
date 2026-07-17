package com.example.navegacionbasicacon4pantallasencompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.navegacionbasicacon4pantallasencompose.ui.theme.NavegacionBasicaCon4PantallasEnComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NavegacionBasicaCon4PantallasEnComposeTheme {
                AppNavigation()
            }
        }
    }
}

class SharedViewModel : ViewModel() {

    var mensaje by mutableStateOf("Sin mensaje")
        private set

    fun enviarMensaje(texto: String) {
        mensaje =
            if (texto.isBlank()) "Sin mensaje"
            else texto
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Menu : Screen("menu")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Details : Screen("details")
}

data class BottomItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

val bottomItems = listOf(
    BottomItem("Home", Icons.Default.Home, Screen.Home.route),
    BottomItem("Menu", Icons.Default.Star, Screen.Menu.route),
    BottomItem("Perfil", Icons.Default.Person, Screen.Profile.route),
    BottomItem("Config", Icons.Default.Settings, Screen.Settings.route),
    BottomItem("Detalles", Icons.Default.Info, Screen.Details.route)
)

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()

    Scaffold(
        bottomBar = {

            val currentRoute =
                navController.currentBackStackEntryAsState()
                    .value?.destination?.route

            NavigationBar {

                bottomItems.forEach { item ->

                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Home.route)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(item.icon, item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Screen.Home.route) {
                BaseScreen(
                    "HOME",
                    R.drawable.home,
                    navController,
                    sharedViewModel
                )
            }

            composable(Screen.Profile.route) {
                BaseScreen(
                    "PERFIL",
                    R.drawable.profile,
                    navController,
                    sharedViewModel
                )
            }

            composable(Screen.Settings.route) {
                BaseScreen(
                    "CONFIGURACIÓN",
                    R.drawable.settings,
                    navController,
                    sharedViewModel
                )
            }

            composable(Screen.Details.route) {
                DetailsScreen(navController, sharedViewModel)
            }

            composable(Screen.Menu.route) {
                MenuScreen(navController, sharedViewModel)
            }
        }
    }
}

@Composable
fun BaseScreen(
    title: String,
    image: Int,
    navController: NavHostController,
    viewModel: SharedViewModel
) {

    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "Winson Fu",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Text(title, fontSize = 24.sp)

        Spacer(Modifier.height(20.dp))

        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier.size(220.dp)
        )

        Spacer(Modifier.height(20.dp))

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Escribe un mensaje") }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.enviarMensaje(text)
                text = ""
            }
        ) {
            Text("Enviar mensaje")
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                navController.navigate(Screen.Details.route)
            }
        ) {
            Text("Ir a Detalles")
        }

        Spacer(Modifier.height(20.dp))

        Text("Mensaje actual:")
        Text(viewModel.mensaje)
    }
}

@Composable
fun DetailsScreen(
    navController: NavHostController,
    viewModel: SharedViewModel
) {

    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "Winson Fu",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        Text("DETAILS", fontSize = 24.sp)

        Spacer(Modifier.height(12.dp))

        Text("Mensaje recibido:")
        Text(viewModel.mensaje)

        Spacer(Modifier.height(20.dp))

        Image(
            painter = painterResource(R.drawable.details),
            contentDescription = null,
            modifier = Modifier.size(220.dp)
        )

        Spacer(Modifier.height(20.dp))

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Nuevo mensaje") }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.enviarMensaje(text)
                text = ""
            }
        ) {
            Text("Actualizar mensaje")
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { navController.popBackStack() }
        ) {
            Text("Volver")
        }
    }
}

@Composable
fun MenuScreen(
    navController: NavHostController,
    viewModel: SharedViewModel
) {

    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "Winson Fu",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Text("Menu", fontSize = 24.sp)

        Spacer(Modifier.height(20.dp))

        Image(
            painter = painterResource(R.drawable.menu),
            contentDescription = null,
            modifier = Modifier.size(220.dp)
        )

        Spacer(Modifier.height(20.dp))

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Escribe un mensaje") }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.enviarMensaje(text)
                text = ""
            }
        ) {
            Text("Enviar mensaje")
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                navController.navigate(Screen.Details.route)
            }
        ) {
            Text("Ir a Detalles")
        }

        Text("Mensaje global:")
        Text(viewModel.mensaje)
    }
}