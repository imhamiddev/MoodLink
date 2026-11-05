package dev.imhamid.moodlink.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val Scheme = lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    secondary = Accent,
    onSecondary = Color.White,
    background = Paper,
    onBackground = Ink,
    surface = Raised,
    onSurface = Ink,
    onSurfaceVariant = InkSoft,
    surfaceContainerLowest = Raised,
    surfaceContainerLow = Raised,
    surfaceContainer = Raised,
    surfaceContainerHigh = Raised,
    surfaceContainerHighest = Raised,
    outline = Line,
    outlineVariant = Line,
    error = Danger,
)

val PanelShape = RoundedCornerShape(14.dp)
val ControlShape = RoundedCornerShape(12.dp)

private val MoodShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(10.dp),
    medium = PanelShape,
    large = PanelShape,
    extraLarge = PanelShape,
)

@Composable
fun MoodLinkTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = MoodTypography, shapes = MoodShapes) {
        Surface(color = Paper, contentColor = Ink) {
            Box(Modifier.fillMaxSize().background(Paper)) { content() }
        }
    }
}
