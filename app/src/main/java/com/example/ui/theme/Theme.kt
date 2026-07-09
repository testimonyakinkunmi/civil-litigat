package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BrassGold,           // D0BCFF (Lavender)
    secondary = WarmAmber,         // FF9F43 (Streak orange)
    tertiary = BentoHighlight,     // 381E72 (Deep Purple)
    background = SlateDark,        // 111318 (Deep Charcoal)
    surface = SlateMedium,         // 1C1B1F (Dark Surface)
    onPrimary = BentoHighlight,    // Contrast text on lavender
    onSecondary = SlateDark,
    onTertiary = BrassGold,
    onBackground = SlateTextLight, // E2E2E6
    onSurface = SlateTextLight,    // E2E2E6
    error = IncorrectRed
)

private val LightColorScheme = lightColorScheme(
    primary = BentoHighlight,      // 381E72 (Deep Purple)
    secondary = BrassGold,         // D0BCFF (Lavender)
    tertiary = SlateLight,         // 44474E (Bento Border)
    background = WarmIvory,        // F5F6F9 (Bento Light Bg)
    surface = androidx.compose.ui.graphics.Color.White,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = BentoHighlight,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
