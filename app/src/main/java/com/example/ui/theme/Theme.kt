package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = EmeraldPrimaryDark,
  onPrimary = OnEmeraldPrimaryDark,
  primaryContainer = EmeraldPrimaryContainerDark,
  onPrimaryContainer = OnEmeraldPrimaryContainerDark,
  secondary = AmberSecondaryDark,
  onSecondary = OnAmberSecondaryDark,
  secondaryContainer = AmberSecondaryContainerDark,
  onSecondaryContainer = OnAmberSecondaryContainerDark,
  tertiary = SlateTertiaryDark,
  onTertiary = OnSlateTertiaryDark,
  tertiaryContainer = SlateTertiaryContainerDark,
  onTertiaryContainer = OnSlateTertiaryContainerDark,
  background = BackgroundDark,
  onBackground = OnBackgroundDark,
  surface = SurfaceDark,
  onSurface = OnSurfaceDark,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = OnSurfaceVariantDark,
  outline = OutlineDark,
  outlineVariant = OutlineVariantDark
)

private val LightColorScheme = lightColorScheme(
  primary = EmeraldPrimaryLight,
  onPrimary = OnEmeraldPrimaryLight,
  primaryContainer = EmeraldPrimaryContainerLight,
  onPrimaryContainer = OnEmeraldPrimaryContainerLight,
  secondary = AmberSecondaryLight,
  onSecondary = OnAmberSecondaryLight,
  secondaryContainer = AmberSecondaryContainerLight,
  onSecondaryContainer = OnAmberSecondaryContainerLight,
  tertiary = SlateTertiaryLight,
  onTertiary = OnSlateTertiaryLight,
  tertiaryContainer = SlateTertiaryContainerLight,
  onTertiaryContainer = OnSlateTertiaryContainerLight,
  background = BackgroundLight,
  onBackground = OnBackgroundLight,
  surface = SurfaceLight,
  onSurface = OnSurfaceLight,
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = OnSurfaceVariantLight,
  outline = OutlineLight,
  outlineVariant = OutlineVariantLight
)

/**
 * Material 3 Application Theme for Algerian Construction & Procurement Management.
 */
@Composable
fun AppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set false to prioritize brand emerald & gold palette
  content: @Composable () -> Unit,
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
    shapes = Shapes,
    content = content
  )
}

/**
 * Backward compatibility alias for [AppTheme].
 */
@Composable
fun AcheteurDZTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) = AppTheme(
  darkTheme = darkTheme,
  dynamicColor = dynamicColor,
  content = content
)
