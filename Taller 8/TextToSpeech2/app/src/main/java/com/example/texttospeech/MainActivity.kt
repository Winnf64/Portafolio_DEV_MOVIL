package com.example.texttospeech

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.texttospeech.ui.theme.TextToSpeechTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TextToSpeechTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TextToSpeechScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun rememberTextToSpeechState(): TtsState {
    val context = LocalContext.current
    val state = remember { TtsState() }

    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                state.tts?.let { engine ->
                    engine.language = Locale.getDefault()
                    val voices = engine.voices
                        ?.filterNotNull()
                        ?.sortedBy { it.name }
                        ?: emptyList()
                    state.voices = voices
                    state.selectedVoice = engine.voice ?: voices.firstOrNull()
                }
                state.isReady = true
            }
        }
        state.tts = tts

        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    return state
}

class TtsState {
    var tts: TextToSpeech? = null
    var isReady by mutableStateOf(false)
    var voices by mutableStateOf<List<Voice>>(emptyList())
    var selectedVoice by mutableStateOf<Voice?>(null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextToSpeechScreen(modifier: Modifier = Modifier) {
    var textToSpeak by remember { mutableStateOf("Hola! Bienvenido a Jetpack Compose.") }
    val ttsState = rememberTextToSpeechState()
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = textToSpeak,
            onValueChange = { textToSpeak = it },
            label = { Text("Escribe el texto a reproducir") },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            singleLine = false,
            maxLines = Int.MAX_VALUE
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Selector de voces ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = ttsState.selectedVoice?.let { "${it.name} (${it.locale})" }
                    ?: "Selecciona una voz",
                onValueChange = {},
                readOnly = true,
                label = { Text("Voz") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (ttsState.voices.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Cargando voces...") },
                        onClick = { },
                        enabled = false
                    )
                } else {
                    ttsState.voices.forEach { voice ->
                        DropdownMenuItem(
                            text = { Text("${voice.name} (${voice.locale})") },
                            onClick = {
                                ttsState.selectedVoice = voice
                                ttsState.tts?.voice = voice
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Botón para reproducir ---
        Button(
            onClick = {
                val engine = ttsState.tts
                if (engine != null && textToSpeak.isNotBlank()) {
                    ttsState.selectedVoice?.let { engine.voice = it }
                    engine.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            },
            enabled = ttsState.isReady && textToSpeak.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reproducir")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { ttsState.tts?.stop() },
            enabled = ttsState.isReady,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Detener")
        }
    }
}