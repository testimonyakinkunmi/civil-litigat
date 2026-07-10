package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val StaticBentoHighlight = androidx.compose.ui.graphics.Color(0xFF381E72)

private val DarkColorScheme = darkColorScheme(
    primary = BrassGold,           // D0BCFF (Lavender)
    secondary = WarmAmber,         // FF9F43 (Streak orange)
    tertiary = StaticBentoHighlight,     // 381E72 (Deep Purple)
    background = SlateDark,        // 111318 (Deep Charcoal)
    surface = SlateMedium,         // 1C1B1F (Dark Surface)
    onPrimary = StaticBentoHighlight,    // Contrast text on lavender
    onSecondary = SlateDark,
    onTertiary = BrassGold,
    onBackground = SlateTextLight, // E2E2E6
    onSurface = SlateTextLight,    // E2E2E6
    error = IncorrectRed
)

private val LightColorScheme = lightColorScheme(
    primary = StaticBentoHighlight,      // 381E72 (Deep Purple)
    secondary = BrassGold,         // D0BCFF (Lavender)
    tertiary = SlateLight,         // 44474E (Bento Border)
    background = WarmIvory,        // F5F6F9 (Bento Light Bg)
    surface = androidx.compose.ui.graphics.Color.White,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = StaticBentoHighlight,
    onTertiary = SlateTextLight,
    onBackground = SlateTextDark,  // 111318
    onSurface = SlateTextDark,     // 111318
    error = IncorrectRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    androidx.compose.runtime.CompositionLocalProvider(LocalThemeIsDark provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
