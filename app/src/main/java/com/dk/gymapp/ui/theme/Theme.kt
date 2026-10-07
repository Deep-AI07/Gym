package com.dk.gymapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

private val DarkColorScheme = darkColorScheme(
    primary = GymGreen,
    onPrimary = Color.Black,
    primaryContainer = GymGreenDark,
    onPrimaryContainer = Color.White,
    secondary = AccentCyan,
    onSecondary = Color.Black,
    tertiary = AccentOrange,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    error = AccentRed
)

private val LightColorScheme = lightColorScheme(
    primary = GymGreenDark,
    onPrimary = Color.White,
    primaryContainer = GymGreenLight,
    onPrimaryContainer = Color.Black,
    secondary = AccentCyan,
    onSecondary = Color.White,
    tertiary = AccentOrange,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceCard,
    onSurfaceVariant = LightTextSecondary,
    outline = LightSurfaceBorder,
    error = AccentRed
)

@Composable
fun GymTheme(
    darkTheme: Boolean = true, // Default to premium dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        val activity = view.context as? ComponentActivity
        SideEffect {
            val window = activity?.window ?: (view.context as? Activity)?.window ?: return@SideEffect

            // Enable modern edge-to-edge transparent system bars
            if (activity != null) {
                val statusBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    )
                }
                val navBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    )
                }
                activity.enableEdgeToEdge(
                    statusBarStyle = statusBarStyle,
                    navigationBarStyle = navBarStyle
                )
            }

            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Disable contrast enforcement on Android 10+ (API 29+) so Android does not paint
            // an artificial dark/translucent scrim over the transparent navigation and status bars
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isStatusBarContrastEnforced = false
                window.isNavigationBarContrastEnforced = false
            }

            val insetsController = WindowCompat.getInsetsController(window, view)

            // Status bar icon colors:
            // When darkTheme is true (dark background), status bar icons (battery, clock, wifi) must be light (white) -> isAppearanceLightStatusBars = false
            // When darkTheme is false (light background), status bar icons must be dark (black) -> isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme

            // GUARANTEE that status bar and navigation bar are ALWAYS visible (never hidden by fullscreen flags)
            // Ensures time, battery, wifi, notifications are always shown!
            insetsController.show(WindowInsetsCompat.Type.statusBars())
            insetsController.show(WindowInsetsCompat.Type.navigationBars())
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
