package com.example.taller5_adivinarelnmero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller5_adivinarelnmero.ui.theme.Taller5AdivinarElNúmeroTheme
import kotlinx.coroutines.delay

data class RankingEntry(val nombre: String, val intentos: Int, val tiempo: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Taller5AdivinarElNúmeroTheme {
                var number by remember { mutableStateOf("") }
                var mensaje by remember { mutableStateOf("") }
                var intentos by remember { mutableStateOf(3) }
                var randomNumber by remember { mutableStateOf((1..10).random()) }
                var segundos by remember { mutableStateOf(0) }
                var corriendo by remember { mutableStateOf(true) }
                var ranking by remember { mutableStateOf(listOf<RankingEntry>()) }
                var nombreInput by remember { mutableStateOf("") }
                var mostrarRanking by remember { mutableStateOf(false) }

                val gano = mensaje == "¡Correcto!"
                val perdio = intentos == 0 && !gano

                // Cronómetro
                LaunchedEffect(corriendo) {
                    while (corriendo) {
                        delay(1000L)
                        segundos++
                    }
                }

                // Detener al terminar
                if (gano || perdio) corriendo = false

                if (mostrarRanking) {
                    // Pantalla de ranking
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text("Ranking", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        if (ranking.isEmpty()) {
                            Text("Aún no hay jugadores en el ranking.")
                        } else {
                            LazyColumn {
                                itemsIndexed(ranking) { i, entry ->
                                    Text(
                                        text = "${i + 1}. ${entry.nombre} — ${entry.intentos} intento(s) — ${entry.tiempo}s",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { mostrarRanking = false }) {
                            Text("Volver")
                        }
                    }
                } else {
                    // Pantalla principal
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ingrese un número del 1 al 10",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Intentos restantes: $intentos")
                        Text(text = "⏱ Tiempo: ${segundos}s")

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            enabled = intentos > 0 && !gano,
                            value = number,
                            onValueChange = { number = it },
                            label = { Text("Ingrese un Número") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo nombre y guardar en ranking al ganar
                        if (gano) {
                            OutlinedTextField(
                                value = nombreInput,
                                onValueChange = { nombreInput = it },
                                label = { Text("Tu nombre para el ranking") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                if (nombreInput.isNotBlank()) {
                                    val nuevaEntrada = RankingEntry(
                                        nombre = nombreInput.trim(),
                                        intentos = 3 - intentos + 1,
                                        tiempo = segundos
                                    )
                                    ranking = (ranking + nuevaEntrada)
                                        .sortedWith(compareBy({ it.intentos }, { it.tiempo }))
                                    nombreInput = ""
                                }
                            }) {
                                Text("Guardar en ranking")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (intentos == 0 || gano) {
                            Button(onClick = {
                                randomNumber = (1..10).random()
                                intentos = 3
                                number = ""
                                mensaje = ""
                                segundos = 0
                                corriendo = true
                            }) {
                                Text("Jugar de nuevo")
                            }
                        }

                        if (intentos > 0 && !gano) {
                            Button(onClick = {
                                val ingresado = number.toIntOrNull()
                                if (ingresado == null || ingresado < 1 || ingresado > 10) {
                                    mensaje = "Ingresa un número entre 1 y 10"
                                } else if (ingresado == randomNumber) {
                                    mensaje = "¡Correcto!"
                                } else if (ingresado > randomNumber) {
                                    mensaje = "El número es menor"
                                    intentos--
                                } else {
                                    mensaje = "El número es mayor"
                                    intentos--
                                }
                                number = ""
                            }) {
                                Text("Adivinar")
                            }
                        }

                        Text(
                            text = if (perdio) "Número incorrecto, suerte en la próxima jugada"
                            else mensaje
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(onClick = { mostrarRanking = true }) {
                            Text("VER RANKING")
                        }
                    }
                }
            }
        }
    }
}