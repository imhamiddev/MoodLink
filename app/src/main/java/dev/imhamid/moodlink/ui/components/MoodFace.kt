package dev.imhamid.moodlink.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.imhamid.moodlink.data.model.Mood

private enum class Eyes { DOTS, WIDE, HEARTS, SLEEPY, ANGRY, TEARY }
private enum class MouthKind { SMILE, GRIN, FROWN, FLAT, OPEN, WAVY }

private data class Face(
    val color: Color,
    val eyes: Eyes,
    val mouth: MouthKind,
    val blush: Boolean,
)

private val Dark = Color(0xFF1E1B16)
private val NoMood = Face(Color(0xFFE4DCCB), Eyes.DOTS, MouthKind.FLAT, false)

private fun Mood.face(): Face = when (this) {
    Mood.HAPPY -> Face(tint, Eyes.DOTS, MouthKind.GRIN, true)
    Mood.LOVE -> Face(tint, Eyes.HEARTS, MouthKind.SMILE, true)
    Mood.CALM -> Face(tint, Eyes.SLEEPY, MouthKind.SMILE, true)
    Mood.SLEEPY -> Face(tint, Eyes.SLEEPY, MouthKind.FLAT, false)
    Mood.TIRED -> Face(tint, Eyes.SLEEPY, MouthKind.WAVY, false)
    Mood.SAD -> Face(tint, Eyes.TEARY, MouthKind.FROWN, false)
    Mood.MISS_YOU -> Face(tint, Eyes.WIDE, MouthKind.FROWN, true)
    Mood.ANGRY -> Face(tint, Eyes.ANGRY, MouthKind.FROWN, false)
    Mood.ANXIOUS -> Face(tint, Eyes.WIDE, MouthKind.WAVY, false)
    Mood.EXCITED -> Face(tint, Eyes.WIDE, MouthKind.OPEN, true)
}

/** A flat, hand-drawn character for each mood (square canvas; pass a size). */
@Composable
fun MoodFace(mood: Mood?, modifier: Modifier = Modifier, squircle: Boolean = false, animated: Boolean = false) {
    val blink: State<Float> = if (animated) rememberBlink(mood) else remember { mutableFloatStateOf(1f) }
    Canvas(modifier) { drawFace(mood?.face() ?: NoMood, squircle, blink.value) }
}

/** Eye openness 1 -> 0.08 -> 1 every few seconds; each mood blinks on its own rhythm. */
@Composable
private fun rememberBlink(mood: Mood?): State<Float> {
    val period = 3200 + (mood?.ordinal ?: 0) * 260
    val t = rememberInfiniteTransition(label = "blink")
    return t.animateFloat(
        initialValue = 1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = period
                1f at 0
                1f at period - 260
                0.08f at period - 150
                1f at period
            },
            RepeatMode.Restart,
        ),
        label = "open",
    )
}

private fun DrawScope.drawFace(f: Face, squircle: Boolean, open: Float) {
    val w = size.minDimension
    if (squircle) drawRoundRect(color = f.color, size = Size(w, w), cornerRadius = CornerRadius(w * .36f))
    else drawCircle(color = f.color, radius = w / 2f, center = Offset(w / 2f, w / 2f))

    drawEyes(f.eyes, w, open)
    drawMouth(f.mouth, w)

    if (f.blush) {
        val blush = Color(0xFFD9506F).copy(alpha = .35f)
        drawCircle(blush, w * .06f, Offset(w * .2f, w * .56f))
        drawCircle(blush, w * .06f, Offset(w * .8f, w * .56f))
    }
}

private fun DrawScope.drawEyes(e: Eyes, w: Float, open: Float) {
    val ey = w * .42f
    val xs = listOf(w * .33f, w * .67f)
    val line = Stroke(width = w * .035f, cap = StrokeCap.Round)
    when (e) {
        Eyes.DOTS -> xs.forEach { x ->
            val r = w * .055f
            drawOval(Dark, Offset(x - r, ey - r * open), Size(2 * r, 2 * r * open))
            if (open > .6f) drawCircle(Color.White, w * .015f, Offset(x - w * .015f, ey - w * .018f))
        }
        Eyes.WIDE -> xs.forEach { x ->
            drawCircle(Color.White, w * .1f, Offset(x, ey))
            val r = w * .05f
            val cy = ey + w * .01f
            drawOval(Dark, Offset(x - r, cy - r * open), Size(2 * r, 2 * r * open))
        }
        Eyes.HEARTS -> xs.forEach { x ->
            drawCircle(Color.White, w * .11f, Offset(x, ey))
            drawHeart(Offset(x, ey), w * .07f, Color(0xFFFF3D6E))
        }
        Eyes.SLEEPY -> xs.forEach { x ->
            drawArc(
                color = Dark, startAngle = 10f, sweepAngle = 160f, useCenter = false,
                topLeft = Offset(x - w * .07f, ey - w * .04f), size = Size(w * .14f, w * .09f), style = line,
            )
        }
        Eyes.ANGRY -> {
            xs.forEach { x -> drawCircle(Dark, w * .05f, Offset(x, ey + w * .02f)) }
            drawLine(Dark, Offset(xs[0] - w * .09f, ey - w * .1f), Offset(xs[0] + w * .07f, ey - w * .04f), w * .035f, StrokeCap.Round)
            drawLine(Dark, Offset(xs[1] + w * .09f, ey - w * .1f), Offset(xs[1] - w * .07f, ey - w * .04f), w * .035f, StrokeCap.Round)
        }
        Eyes.TEARY -> {
            xs.forEach { x -> drawCircle(Dark, w * .055f, Offset(x, ey)) }
            drawCircle(Color(0xFF9CC3FF), w * .035f, Offset(xs[0] - w * .02f, ey + w * .13f))
        }
    }
}

private fun DrawScope.drawMouth(m: MouthKind, w: Float) {
    val line = Stroke(width = w * .035f, cap = StrokeCap.Round)
    when (m) {
        MouthKind.SMILE -> drawArc(
            color = Dark, startAngle = 20f, sweepAngle = 140f, useCenter = false,
            topLeft = Offset(w * .39f, w * .55f), size = Size(w * .22f, w * .14f), style = line,
        )
        MouthKind.GRIN -> {
            drawArc(
                color = Dark, startAngle = 0f, sweepAngle = 180f, useCenter = true,
                topLeft = Offset(w * .37f, w * .52f), size = Size(w * .26f, w * .2f),
            )
            drawCircle(Color(0xFFFF7A9A), w * .04f, Offset(w * .5f, w * .69f))
        }
        MouthKind.FROWN -> drawArc(
            color = Dark, startAngle = 200f, sweepAngle = 140f, useCenter = false,
            topLeft = Offset(w * .4f, w * .64f), size = Size(w * .2f, w * .12f), style = line,
        )
        MouthKind.FLAT -> drawLine(Dark, Offset(w * .43f, w * .66f), Offset(w * .57f, w * .66f), w * .035f, StrokeCap.Round)
        MouthKind.OPEN -> drawOval(Dark, Offset(w * .43f, w * .58f), Size(w * .14f, w * .17f))
        MouthKind.WAVY -> {
            val p = Path().apply {
                moveTo(w * .38f, w * .67f)
                quadraticBezierTo(w * .44f, w * .6f, w * .5f, w * .67f)
                quadraticBezierTo(w * .56f, w * .74f, w * .62f, w * .67f)
            }
            drawPath(p, Dark, style = line)
        }
    }
}

private fun DrawScope.drawHeart(c: Offset, s: Float, color: Color) {
    val p = Path().apply {
        moveTo(c.x, c.y + s * .9f)
        cubicTo(c.x - s * 1.4f, c.y, c.x - s * .7f, c.y - s * 1.0f, c.x, c.y - s * .3f)
        cubicTo(c.x + s * .7f, c.y - s * 1.0f, c.x + s * 1.4f, c.y, c.x, c.y + s * .9f)
        close()
    }
    drawPath(p, color)
}
