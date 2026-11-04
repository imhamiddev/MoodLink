package dev.imhamid.moodlink.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer

/** The friendly marshmallow mascot on the welcome screen, gently floating. */
@Composable
fun Mascot(modifier: Modifier = Modifier) {
    val t = rememberInfiniteTransition(label = "mascot")
    val float by t.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float",
    )
    Canvas(modifier.graphicsLayer { translationY = float * 8.dp.toPx() }) {
        val w = size.width
        val h = size.height

        // warm glow shape behind the body
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFFFD08A), Color(0xFFFF8F5A)), startY = h * .05f, endY = h * .95f),
            topLeft = Offset(w * .1f, h * .04f), size = Size(w * .8f, h * .9f),
            cornerRadius = CornerRadius(w * .24f),
        )
        // body
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFEBE0FF)), startY = h * .2f, endY = h * .95f),
            topLeft = Offset(w * .18f, h * .16f), size = Size(w * .64f, h * .76f),
            cornerRadius = CornerRadius(w * .3f),
        )
        // cheeks
        val cheek = Color(0xFFFF9EC0).copy(alpha = .65f)
        drawCircle(cheek, w * .045f, Offset(w * .3f, h * .5f))
        drawCircle(cheek, w * .045f, Offset(w * .7f, h * .5f))
        // eyes
        listOf(w * .38f, w * .62f).forEach { x ->
            drawCircle(Color(0xFF241638), w * .04f, Offset(x, h * .42f))
            drawCircle(Color.White, w * .012f, Offset(x - w * .012f, h * .405f))
        }
        // open smile
        drawArc(
            color = Color(0xFF2B1B3E), startAngle = 0f, sweepAngle = 180f, useCenter = true,
            topLeft = Offset(w * .44f, h * .45f), size = Size(w * .12f, h * .1f),
        )
        drawArc(
            color = Color(0xFFFF6F8E), startAngle = 20f, sweepAngle = 140f, useCenter = true,
            topLeft = Offset(w * .465f, h * .49f), size = Size(w * .07f, h * .055f),
        )
        // purple hands
        val hand = Brush.radialGradient(listOf(Color(0xFFB9A9FF), Color(0xFF7C6BEA)), center = Offset(w * .1f, h * .6f), radius = w * .12f)
        drawCircle(hand, w * .075f, Offset(w * .13f, h * .62f))
        drawCircle(hand, w * .075f, Offset(w * .87f, h * .52f))
        // mood line running across the body
        val line = Path().apply {
            moveTo(w * .06f, h * .72f)
            lineTo(w * .26f, h * .63f)
            lineTo(w * .4f, h * .77f)
            lineTo(w * .6f, h * .62f)
            lineTo(w * .74f, h * .7f)
            lineTo(w * .94f, h * .52f)
        }
        drawPath(line, Color(0xFF6B4DF0), style = Stroke(width = w * .012f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
