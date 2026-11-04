package dev.imhamid.moodlink.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val Scheme = lightColorScheme(
    primary = Purple500,
    onPrimary = Color.White,
    secondary = Blue500,
    background = Lavender100,
    onBackground = Ink,
    surface = GlassWhite,
    onSurface = Ink,
    onSurfaceVariant = InkSoft,
    error = Danger,
)

private val MoodShapes = Shapes(
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
)

val ScreenGradient = Brush.verticalGradient(listOf(PinkBlush, Lavender100, Lavender200))
val CtaGradient = Brush.horizontalGradient(listOf(Purple600, Blue500))

@Composable
fun MoodLinkTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = MoodTypography, shapes = MoodShapes) {
        Box(Modifier.fillMaxSize().background(ScreenGradient)) { content() }
    }
}
