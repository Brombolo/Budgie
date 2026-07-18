package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryContainerGreen,
    secondary = SecondaryContainerYellow,
    tertiary = TertiaryContainerBlue,
    background = OnSurfaceDark,
    surface = Color(0xFF2A2B2B),
    surfaceVariant = Color(0xFF383A3A),
    onPrimary = OnSurfaceDark,
    onSecondary = OnSurfaceDark,
    onTertiary = OnSurfaceDark,
    onBackground = BackgroundCream,
    onSurface = BackgroundCream,
    onSurfaceVariant = Color(0xFFC2C9C3),
    outline = Color(0xFF8C938D),
    outlineVariant = Color(0xFF414942),
    error = ErrorRed,
    errorContainer = ErrorContainerRed
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryGreen,
    primaryContainer = PrimaryContainerGreen,
    secondary = SecondaryYellow,
    secondaryContainer = SecondaryContainerYellow,
    tertiary = TertiaryBlue,
    tertiaryContainer = TertiaryContainerBlue,
    background = BackgroundCream,
    surface = Color.White,
    surfaceVariant = Color(0xFFEFECE9),
    surfaceContainerLowest = SurfaceLowestWhite,
    surfaceContainerHigh = SurfaceContainerHigh,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineGrey,
    outlineVariant = Color(0xFFD2D5D2),
    error = ErrorRed,
    errorContainer = ErrorContainerRed,
    onError = Color.White
  )

@Composable
fun BudgieTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic colors to keep our beautiful custom Budgie pop-pastel theme consistent
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  BudgieTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

