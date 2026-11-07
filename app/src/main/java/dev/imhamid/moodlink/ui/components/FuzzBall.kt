package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import dev.imhamid.moodlink.R
import kotlin.math.floor
import kotlin.math.min

private val Red = Color(0xFFD4342E)
private val Yellow = Color(0xFFEEBE48)
private val Green = Color(0xFF68B064)

/** The slider colour for a position from 0 (terrible, red) to 1 (excellent, green). Matches the plush frames. */
fun spectrumColor(position: Float): Color {
    val p = position.coerceIn(0f, 1f)
    return when {
        p < .4f -> lerp(Red, Yellow, p / .4f)
        p < .8f -> lerp(Yellow, Green, (p - .4f) / .4f)
        else -> Green
    }
}

// One rendered plush per mood, worst to best, in the same order as Mood.Spectrum.
private val Frames = listOf(
    R.drawable.fuzz_0, R.drawable.fuzz_1, R.drawable.fuzz_2, R.drawable.fuzz_3, R.drawable.fuzz_4,
    R.drawable.fuzz_5, R.drawable.fuzz_6, R.drawable.fuzz_7, R.drawable.fuzz_8, R.drawable.fuzz_9,
)

/**
 * The soft furry ball. [position] (0..1) walks through ten pre-rendered frames, and the two frames
 * around it are cross-faded, so colour and face change smoothly while you drag.
 */
@Composable
fun FuzzBall(position: Float, modifier: Modifier = Modifier) {
    val p = position.coerceIn(0f, 1f) * (Frames.size - 1)
    val i = min(floor(p).toInt(), Frames.size - 2)
    val t = p - i
    Box(modifier, contentAlignment = Alignment.Center) {
        // soft contact shadow, flattened into an ellipse
        Canvas(Modifier.fillMaxSize()) {
            val side = min(size.width, size.height)
            val center = Offset(size.width / 2f, size.height / 2f + side * .40f)
            withTransform({ scale(1f, .22f, pivot = center) }) {
                drawCircle(
                    Brush.radialGradient(
                        listOf(Color.Black.copy(alpha = .22f), Color.Transparent),
                        center = center, radius = side * .34f,
                    ),
                    radius = side * .34f, center = center,
                )
            }
        }
        Image(painterResource(Frames[i]), null, Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        if (t > .01f) {
            Image(painterResource(Frames[i + 1]), null, Modifier.fillMaxSize().alpha(t), contentScale = ContentScale.Fit)
        }
    }
}
