package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigoLight,
    onPrimary = Color.White,
    primaryContainer = PrimaryIndigoDark,
    onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = SecondaryTealLight,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryTeal,
    onSecondaryContainer = Color(0xFFE0F2F1),
    tertiary = ReputationGoldLight,
    onTertiary = Color.Black,
    error = Color(0xFFFFB4AB),
    background = BackgroundDark,
    onBackground = Color(0xFFE2E2E9),
    surface = SurfaceDark,
    onSurface = Color(0xFFE2E2E9),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC7CBD6),
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E3FF),
    onPrimaryContainer = PrimaryIndigoDark,
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF004D40),
    tertiary = ReputationGold,
    onTertiary = Color.Black,
    error = Color(0xFFBA1A1A),
    background = BackgroundLight,
    onBackground = Color(0xFF191C20),
    surface = SurfaceLight,
    onSurface = Color(0xFF191C20),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF44474F),
    outline = OutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep distinctive brand theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
