package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color as ComposeColor

val LocalThemeIsDark = staticCompositionLocalOf { true }

// Custom dynamic Color object to shadow androidx.compose.ui.graphics.Color in themed files
object Color {
    val White: ComposeColor
        @Composable
        @ReadOnlyComposable
        get() = if (LocalThemeIsDark.current) ComposeColor.White else ComposeColor(0xFF111318)

    val Transparent = ComposeColor.Transparent
    val LightGray: ComposeColor
        @Composable
        @ReadOnlyComposable
        get() = if (LocalThemeIsDark.current) ComposeColor.LightGray else ComposeColor(0xFF6C6C70)

    val Red = ComposeColor.Red
    val Green = ComposeColor.Green

    operator fun invoke(color: Long): ComposeColor {
        return ComposeColor(color)
    }

    @OptIn(kotlin.ExperimentalUnsignedTypes::class)
    operator fun invoke(color: ULong): ComposeColor {
        return ComposeColor(color)
    }

    operator fun invoke(r: Int, g: Int, b: Int, a: Int = 255): ComposeColor {
        return ComposeColor(r, g, b, a)
    }
}

// Bento Grid Theme: Deep Charcoal, Lavender Accent, and Royal Deep Purple
val SlateDark = ComposeColor(0xFF111318)      // Bento dark primary background
val SlateMedium = ComposeColor(0xFF1C1B1F)    // Bento card background
val SlateLight = ComposeColor(0xFF44474E)     // Bento dark border/divider color
val BrassGold = ComposeColor(0xFFD0BCFF)      // Bento light purple/lavender accent
val WarmAmber = ComposeColor(0xFFFF9F43)      // Bento streak fire orange
val WarmIvory = ComposeColor(0xFFF5F6F9)      // Bento light mode background
val SlateTextDark = ComposeColor(0xFF111318)  // Bento light mode text
val SlateTextLight = ComposeColor(0xFFE2E2E6) // Bento dark mode text

// Additional Bento Theme Color Tokens
val BentoBg: ComposeColor
    @Composable
    @ReadOnlyComposable
    get() = if (LocalThemeIsDark.current) ComposeColor(0xFF111318) else ComposeColor(0xFFF5F6F9)

val BentoSurface: ComposeColor
    @Composable
    @ReadOnlyComposable
    get() = if (LocalThemeIsDark.current) ComposeColor(0xFF1C1B1F) else ComposeColor(0xFFFFFFFF)

val BentoBorder: ComposeColor
    @Composable
    @ReadOnlyComposable
    get() = if (LocalThemeIsDark.current) ComposeColor(0xFF44474E) else ComposeColor(0xFFD2D2D7)

val BentoAccent: ComposeColor
    @Composable
    @ReadOnlyComposable
    get() = if (LocalThemeIsDark.current) ComposeColor(0xFFD0BCFF) else ComposeColor(0xFF5856D6)

val BentoHighlight: ComposeColor
    @Composable
    @ReadOnlyComposable
    get() = if (LocalThemeIsDark.current) ComposeColor(0xFF381E72) else ComposeColor(0xFFE5E5EA)

val BentoDarkAccent: ComposeColor
    @Composable
    @ReadOnlyComposable
    get() = if (LocalThemeIsDark.current) ComposeColor(0xFF2D2F33) else ComposeColor(0xFFEFEFF4)

val BentoSubtext: ComposeColor
    @Composable
    @ReadOnlyComposable
    get() = if (LocalThemeIsDark.current) ComposeColor(0xFF919094) else ComposeColor(0xFF6C6C70)

val BentoText: ComposeColor
    @Composable
    @ReadOnlyComposable
    get() = if (LocalThemeIsDark.current) ComposeColor(0xFFE2E2E6) else ComposeColor(0xFF1C1C1E)

// Utility Colors for feedback & corrections
val CorrectGreen = ComposeColor(0xFF2ECC71)   // Success
val IncorrectRed = ComposeColor(0xFFE74C3C)   // Error/Correction
val SoftGreen = ComposeColor(0xFFD5F5E3)      // Light mode correct card tint
val SoftRed = ComposeColor(0xFFFADBD8)        // Light mode error card tint
