package com.example.tl7_ahorcado_fu_huertas.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary        = Blood,
    onPrimary      = Bone,
    primaryContainer   = BloodDark,
    onPrimaryContainer = Bone,
    secondary      = BoneDim,
    onSecondary    = Gallows,
    background     = Gallows,
    onBackground   = Bone,
    surface        = Rope,
    onSurface      = Bone,
    surfaceVariant = Ash,
    onSurfaceVariant = BoneDim,
    outline        = Ash,
    error          = BloodLight,
    onError        = Bone
)

@Composable
fun TL7_Ahorcado_FuHuertasTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Gallows.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography,
        content     = content
    )
}
