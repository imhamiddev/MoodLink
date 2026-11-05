package dev.imhamid.moodlink.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
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
    small = RoundedCornerShape(20.dp),
    medium = RoundedCornerShape(28.dp),
    large = RoundedCornerShape(36.dp),
)

val ScreenGradient = Brush.verticalGradient(
    listOf(Color(0xFFF1E4FF), Color(0xFFFBE5F0), Color(0xFFE4E1FF)),
)
val CtaGradient = Brush.horizontalGradient(listOf(Purple600, Blue500))

@Composable
fun MoodLinkTheme(content: @Composable () -> Unit) {
    val drift = rememberInfiniteTransition(label = "ambient")
    val d by drift.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(11_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "drift",
    )
    MaterialTheme(colorScheme = Scheme, typography = MoodTypography, shapes = MoodShapes) {
        Surface(color = Color.Transparent, contentColor = Ink) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(ScreenGradient)
                    .drawBehind {
                        // Soft colour glows, like the blurred backgrounds in the references.
                        drawRect(
                            Brush.radialGradient(
                                listOf(Color(0x66B58CFF), Color.Transparent),
                                center = Offset(size.width * (.05f + .35f * d), size.height * (.05f + .08f * d)),
                                radius = size.width * .95f,
                            )
                        )
                        drawRect(
                            Brush.radialGradient(
                                listOf(Color(0x66FFB3D1), Color.Transparent),
                                center = Offset(size.width * (.95f - .35f * d), size.height * (.92f - .1f * d)),
                                radius = size.width * 1.05f,
                            )
                        )
                    },
            ) { content() }
        }
    }
}
