package com.example.myapplication1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication1.ui.theme.MyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                var num1 by remember {mutableStateOf("0")}
                var num2 by remember {mutableStateOf("0")}
                var resultado by remember {mutableStateOf("—")}

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Calculadora",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TextField(
                        value = num1,
                        onValueChange = { num1 = it },
                        label = { Text("Valor A") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = num2,
                        onValueChange = { num2 = it },
                        label = { Text("Valor B") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Resultado: $resultado",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        val a = num1.toDoubleOrNull()
                        val b = num2.toDoubleOrNull()
                        resultado = if (a != null && b != null) {
                            val suma = a + b
                            if (suma % 1.0 == 0.0) suma.toInt().toString() else suma.toString()
                        } else {
                            "Error"
                        }
                    }) {
                        Text("SUMAR")
                    }
                }
            }
        }
    }
}