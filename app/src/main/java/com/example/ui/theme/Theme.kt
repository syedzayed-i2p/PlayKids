package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = KidBlue,
    onPrimary = SurfaceCard,
    primaryContainer = KidBlueLight,
    onPrimaryContainer = KidBlueDark,
    secondary = KidOrange,
    onSecondary = SurfaceCard,
    secondaryContainer = KidOrangeLight,
    onSecondaryContainer = KidOrangeDark,
    tertiary = KidGreen,
    onTertiary = SurfaceCard,
    tertiaryContainer = KidGreenLight,
    onTertiaryContainer = KidGreenDark,
    background = BgCream,
    onBackground = TextDark,
    surface = SurfaceCard,
    onSurface = TextDark,
    surfaceVariant = KidYellowLight,
    onSurfaceVariant = TextDark
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BgCream.toArgb()
            window.navigationBarColor = BgCream.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
