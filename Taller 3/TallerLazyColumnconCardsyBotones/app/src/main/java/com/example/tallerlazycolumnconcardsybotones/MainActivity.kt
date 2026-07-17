package com.example.tallerlazycolumnconcardsybotones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.tallerlazycolumnconcardsybotones.ui.theme.TallerLazyColumnConCardsYBotonesTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.*
import androidx.navigation.NavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TallerLazyColumnConCardsYBotonesTheme {
                AppNavigation()
            }
        }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val favorites = remember { mutableStateListOf<String>() }

    NavHost(navController, startDestination = "list") {
        composable("list") {
            CountryListScreen(navController, favorites)
        }
        composable("favorites") {
            FavoriteScreen(navController, favorites)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListScreen(
    navController: NavController,
    favorites: MutableList<String>
) {
    val countries = listOf(
        "Panama", "Mexico", "Argentina", "Chile", "Colombia",
        "Espana", "Italia", "Francia", "Japon", "Brasil",
        "Peru", "Costa Rica", "Alemania", "Portugal", "China",
        "Butan", "Vietnam", "El Salvador", "Nicaragua",
        "Corea del Sur", "Australia", "India", "Canada",
        "Estados Unidos", "Uruguay", "Ecuador", "Bolivia",
        "Venezuela", "Cuba", "Republica Dominicana",
        "Guatemala", "Honduras"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paises del Mundo",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Button(
                onClick = { navController.navigate("favorites") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(text = "Ver Favoritos (${favorites.size})")
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(countries) { country ->
                    CountryCard(
                        country = country,
                        isFavorite = favorites.contains(country),
                        onFavoriteClick = {
                            if (favorites.contains(country)) {
                                favorites.remove(country)
                            } else {
                                favorites.add(country)
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun CountryCard(
    country: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        border = BorderStroke(2.dp, Color(0xFF1565C0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = country,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isFavorite) "Guardado en favoritos" else "Toca Favorito para guardar",
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Button(
                    onClick = {
                        Toast.makeText(context, "Pais: $country", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(text = "Detalles")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onFavoriteClick) {
                    Text(text = if (isFavorite) "Quitar" else "Favorito")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navController: NavController,
    favorites: List<String>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Favoritos",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text(
                            text = "<",
                            fontSize = 22.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes paises favoritos aun.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(favorites) { country ->
                    FavoriteCard(country = country)
                }
            }
        }
    }
}


@Composable
fun FavoriteCard(country: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        border = BorderStroke(2.dp, Color(0xFF1565C0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = country,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}