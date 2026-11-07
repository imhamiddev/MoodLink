package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private val Red = Color(0xFFE5483F)
private val Yellow = Color(0xFFF0C24B)
private val Green = Color(0xFF6FB86E)

/** The slider colour for a position from 0 (terrible, red) to 1 (excellent, green). */
fun spectrumColor(position: Float): Color {
    val p = position.coerceIn(0f, 1f)
    return if (p < .5f) lerp(Red, Yellow, p / .5f) else lerp(Yellow, Green, (p - .5f) / .5f)
}

private class Strand(val dirX: Float, val dirY: Float, val start: Float, val length: Float, val light: Boolean)

private fun buildStrands(): List<Strand> {
    val rnd = Random(7)
    return List(1500) {
        val angle = rnd.nextFloat() * 2f * PI.toFloat()
        val start = sqrt(rnd.nextFloat()) * .98f
        val length = (.05f + rnd.nextFloat() * .08f) * (if (start > .85f) 1.8f else 1f)
        Strand(cos(angle), sin(angle), start, length, rnd.nextBoolean())
    }
}

/**
 * A fuzzy ball whose colour and face follow [position] (0 terrible .. 1 excellent):
 * red with a frown and angry brows at the bottom, yellow in the middle, green and smiling at the top.
 */
@Composable
fun FuzzBall(position: Float, modifier: Modifier = Modifier) {
    val strands = remember { buildStrands() }
    val color = spectrumColor(position)
    Canvas(modifier) {
        val d = min(size.width, size.height)
        val r = d * .36f
        val c = Offset(size.width / 2f, size.height * .46f)

        drawOval(
            Color.Black.copy(alpha = .10f),
            topLeft = Offset(c.x - r * .9f, c.y + r * 1.08f),
            size = Size(r * 1.8f, r * .22f),
        )
        drawCircle(
            Brush.radialGradient(
                listOf(lerp(color, Color.White, .25f), color, lerp(color, Color.Black, .18f)),
                center = Offset(c.x - r * .25f, c.y - r * .3f), radius = r * 1.4f,
            ),
            radius = r, center = c,
        )

        val light = ArrayList<Offset>(strands.size)
        val dark = ArrayList<Offset>(strands.size)
        strands.forEach { s ->
            val a = Offset(c.x + s.dirX * s.start * r, c.y + s.dirY * s.start * r)
            val b = Offset(a.x + s.dirX * s.length * r, a.y + s.dirY * s.length * r)
            val list = if (s.light) light else dark
            list.add(a); list.add(b)
        }
        val hair = max(1f, d * .0035f)
        drawPoints(light, PointMode.Lines, lerp(color, Color.White, .5f).copy(alpha = .55f), hair, StrokeCap.Round)
        drawPoints(dark, PointMode.Lines, lerp(color, Color.Black, .3f).copy(alpha = .45f), hair, StrokeCap.Round)

        // face
        val ink = Color(0xFF14110E)
        val eyeY = c.y - r * .08f
        val eyeDx = r * .36f
        val squint = ((.35f - position) / .35f).coerceIn(0f, 1f)
        val eyeW = r * .15f
        val eyeH = r * (.15f - .05f * squint)
        for (side in listOf(-1f, 1f)) {
            val ex = c.x + side * eyeDx
            drawOval(ink, Offset(ex - eyeW / 2f, eyeY - eyeH / 2f), Size(eyeW, eyeH))
            drawCircle(Color.White.copy(alpha = .85f), eyeW * .14f, Offset(ex - eyeW * .12f, eyeY - eyeH * .18f))
            if (squint > 0f) {
                val outer = Offset(ex + side * r * .16f, eyeY - r * .17f)
                val inner = Offset(ex - side * r * .12f, eyeY - r * .17f + squint * r * .10f)
                drawLine(ink.copy(alpha = .8f), outer, inner, strokeWidth = r * .04f, cap = StrokeCap.Round)
            }
        }

        val k = (position - .5f) * 2f
        val mw = r * (.16f + .10f * kotlin.math.abs(k))
        val my = c.y + r * .30f
        val mouth = Path().apply {
            moveTo(c.x - mw, my)
            quadraticBezierTo(c.x, my + k * r * .20f, c.x + mw, my)
        }
        drawPath(mouth, ink, style = Stroke(width = r * .035f, cap = StrokeCap.Round))
    }
}
