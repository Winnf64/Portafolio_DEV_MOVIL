package com.example.tl7_ahorcado_fu_huertas

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tl7_ahorcado_fu_huertas.ui.theme.Ash
import com.example.tl7_ahorcado_fu_huertas.ui.theme.Blood
import com.example.tl7_ahorcado_fu_huertas.ui.theme.BloodDark
import com.example.tl7_ahorcado_fu_huertas.ui.theme.Bone
import com.example.tl7_ahorcado_fu_huertas.ui.theme.BoneDim
import com.example.tl7_ahorcado_fu_huertas.ui.theme.Gallows
import com.example.tl7_ahorcado_fu_huertas.ui.theme.Rope
import com.example.tl7_ahorcado_fu_huertas.ui.theme.WinGold
import com.example.tl7_ahorcado_fu_huertas.ui.theme.TL7_Ahorcado_FuHuertasTheme

// ─── ViewModel ───────────────────────────────────────────────────────────────

class GameViewModel : ViewModel() {
    var secretWord  by mutableStateOf("")
    var hiddenText  by mutableStateOf(CharArray(0))
    var errors      by mutableIntStateOf(0)
    val guessedChars = mutableSetOf<Char>()

    fun startGame() {
        hiddenText = CharArray(secretWord.length) { '_' }
        errors = 0
        guessedChars.clear()
    }

    fun hasBeenGuessed(char: Char) = guessedChars.contains(char)

    fun makeGuess(guessedChar: Char) {
        guessedChars.add(guessedChar)
        var charInSecret = false
        val newHiddenText = hiddenText.clone()
        hiddenText.forEachIndexed { i, char ->
            if (char == '_' && guessedChar == secretWord[i]) {
                charInSecret = true
                newHiddenText[i] = guessedChar
            }
        }
        if (!charInSecret) errors++
        hiddenText = newHiddenText
    }

    fun isSecretWordGuessed() = !hiddenText.contains('_')
    fun isGameOver()          = errors == 6 || isSecretWordGuessed()
}

// ─── Activity ────────────────────────────────────────────────────────────────

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TL7_Ahorcado_FuHuertasTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Gallows
                ) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// ─── Componentes comunes ──────────────────────────────────────────────────────

@Composable
fun GameTitle(subtitle: String? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "AHORCADO",
            style = MaterialTheme.typography.displayLarge,
            color = Blood,
            textAlign = TextAlign.Center
        )
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = BoneDim,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) Blood else Ash,
            contentColor   = Bone
        )
    ) {
        Text(
            text  = text,
            style = MaterialTheme.typography.labelLarge,
            color = Bone
        )
    }
}

// ─── StartScreen ─────────────────────────────────────────────────────────────

@Composable
fun StartScreen(navController: NavController, game: GameViewModel) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gallows),
        contentAlignment = Alignment.Center
    ) {
        // Línea decorativa lateral
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
                .size(2.dp, 260.dp)
                .background(BloodDark)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            GameTitle(subtitle = "Ingresa la palabra secreta")

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value       = game.secretWord,
                onValueChange = { newValue ->
                    if (newValue.all { it.isLetter() }) game.secretWord = newValue.lowercase()
                },
                modifier    = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Palabra secreta...", color = BoneDim)
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine  = true,
                shape       = RoundedCornerShape(4.dp),
                colors      = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Blood,
                    unfocusedBorderColor = Ash,
                    focusedTextColor     = Bone,
                    unfocusedTextColor   = Bone,
                    cursorColor          = Blood,
                    focusedContainerColor   = Rope,
                    unfocusedContainerColor = Rope
                )
            )

            GameButton(text = "JUGAR", onClick = {
                if (game.secretWord.isEmpty()) {
                    Toast.makeText(context, "El campo no puede estar vacío.", Toast.LENGTH_SHORT).show()
                } else {
                    game.startGame()
                    navController.navigate("game")
                }
            })
        }

        // Línea decorativa inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(60.dp, 1.dp)
                .background(BloodDark)
        )
    }
}

// ─── GameScreen ───────────────────────────────────────────────────────────────

@Composable
fun GameScreen(navController: NavController, game: GameViewModel) {
    val context = LocalContext.current
    val drawables = listOf<Int>(
        R.drawable.i0, R.drawable.i1, R.drawable.i2,
        R.drawable.i3, R.drawable.i4, R.drawable.i5, R.drawable.i6
    )
    var guessedChar by remember { mutableStateOf("") }
    val gameOver    = game.isGameOver()
    val won         = game.isSecretWordGuessed()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gallows)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GameTitle()

            // Imagen del ahorcado
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Rope)
                    .border(1.dp, Ash, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(drawables[game.errors.coerceAtMost(6)]),
                    contentDescription = "Ahorcado — ${game.errors} errores",
                    modifier = Modifier.size(200.dp)
                )
            }

            // Palabra oculta
            Text(
                text = if (gameOver) game.secretWord
                       else game.hiddenText.concatToString().map { "$it " }.joinToString(""),
                style = MaterialTheme.typography.titleLarge.copy(
                    letterSpacing = 6.sp,
                    textDecoration = if (gameOver && !won) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (gameOver && won) WinGold else Bone,
                textAlign = TextAlign.Center
            )

            // Contador de errores
            Text(
                text  = "Errores: ${game.errors} / 6",
                style = MaterialTheme.typography.bodyLarge,
                color = if (game.errors >= 4) Blood else BoneDim
            )

            // Mensaje fin de juego
            if (gameOver) {
                Text(
                    text  = if (won) "¡GANASTE!" else "PERDISTE",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (won) WinGold else Blood,
                    textAlign = TextAlign.Center
                )
            }

            // Campo de entrada (oculto si el juego terminó)
            if (!gameOver) {
                OutlinedTextField(
                    value         = guessedChar,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1 && newValue.all { it.isLetter() })
                            guessedChar = newValue.lowercase()
                    },
                    modifier    = Modifier.fillMaxWidth(),
                    placeholder = { Text("Letra...", color = BoneDim) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine  = true,
                    shape       = RoundedCornerShape(4.dp),
                    colors      = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = Blood,
                        unfocusedBorderColor    = Ash,
                        focusedTextColor        = Bone,
                        unfocusedTextColor      = Bone,
                        cursorColor             = Blood,
                        focusedContainerColor   = Rope,
                        unfocusedContainerColor = Rope
                    )
                )
            }

            Spacer(Modifier.weight(1f))

            GameButton(
                text    = if (gameOver) "CONTINUAR" else "PROBAR",
                onClick = {
                    if (gameOver) {
                        game.secretWord = ""
                        navController.navigate("start")
                    } else {
                        if (guessedChar.isNotEmpty()) {
                            if (game.hasBeenGuessed(guessedChar.first())) {
                                Toast.makeText(context, "Ya intentaste la letra '${guessedChar}'", Toast.LENGTH_SHORT).show()
                            } else {
                                game.makeGuess(guessedChar.first())
                            }
                            guessedChar = ""
                        }
                    }
                }
            )
        }
    }
}

// ─── Navegación ───────────────────────────────────────────────────────────────

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val game          = viewModel<GameViewModel>()

    NavHost(
        navController    = navController,
        startDestination = "start",
        modifier         = modifier
    ) {
        composable("start") { StartScreen(navController, game) }
        composable("game")  { GameScreen(navController, game)  }
    }
}
