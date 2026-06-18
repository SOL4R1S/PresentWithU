package com.proclaimer.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val GradientPrimary: List<Color> = listOf(Color(0xFF6C63FF), Color(0xFF3D35A0))
val GradientSecondary: List<Color> = listOf(Color(0xFF03DAC6), Color(0xFF005048))
val GradientWarm: List<Color> = listOf(Color(0xFFFFB74D), Color(0xFF6C63FF))

// --- Proclaimer Dark Theme ---
// Inspired by church AV environments: low-light friendly, high contrast text

val ProclaimerDark = darkColorScheme(
    primary = Color(0xFF6C63FF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF3D35A0),
    onPrimaryContainer = Color(0xFFE8E6FF),

    secondary = Color(0xFF03DAC6),
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF005048),
    onSecondaryContainer = Color(0xFF4FF8E6),

    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF3A2500),
    tertiaryContainer = Color(0xFF543600),
    onTertiaryContainer = Color(0xFFFFDDB3),

    error = Color(0xFFEF5350),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF0D0D1A),
    onBackground = Color(0xFFE8E8F0),
    surface = Color(0xFF151528),
    onSurface = Color(0xFFE8E8F0),
    surfaceVariant = Color(0xFF1E1E38),
    onSurfaceVariant = Color(0xFFC4C4D0),
    outline = Color(0xFF3E3E5C),
    outlineVariant = Color(0xFF2A2A44),

    inverseSurface = Color(0xFFE8E8F0),
    inverseOnSurface = Color(0xFF1E1E2E),
    inversePrimary = Color(0xFF4A42C0)
)

val ProclaimerLight = lightColorScheme(
    primary = Color(0xFF4A42C0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE8E6FF),
    onPrimaryContainer = Color(0xFF100078),

    secondary = Color(0xFF005048),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF4FF8E6),
    onSecondaryContainer = Color(0xFF00201C),

    tertiary = Color(0xFF7A5300),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDDB3),
    onTertiaryContainer = Color(0xFF271800),

    background = Color(0xFFF5F5FA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)

// Typography for clean, readable presentation text
val ProclaimerTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun GradientBox(
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(gradient)
        ),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun GradientSurface(
    gradient: List<Color> = GradientPrimary,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface
    ) {
        GradientBox(gradient = gradient, content = content)
    }
}

@Composable
fun ProclaimerTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ProclaimerDark else ProclaimerLight

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ProclaimerTypography,
        content = content
    )
}
