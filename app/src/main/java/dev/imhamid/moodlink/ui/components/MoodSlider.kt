package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.ui.theme.InkSoft

private val Rail = Color(0xFFE9ECE4)

/**
 * A 0..1 slider with a red, yellow, green fill and a white grip. The track always runs left (bad)
 * to right (good), also in Persian, because the colours carry the meaning.
 */
@Composable
fun MoodSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    lowLabel: String,
    highLabel: String,
    stateText: String,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        val onChange by rememberUpdatedState(onValueChange)
        Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Canvas(
                Modifier.fillMaxWidth().height(48.dp)
                    .semantics {
                        stateDescription = stateText
                        progressBarRangeInfo = ProgressBarRangeInfo(value, 0f..1f)
                        setProgress { onChange(it.coerceIn(0f, 1f)); true }
                    }
                    .pointerInput(Unit) {
                        val grip = 36.dp.toPx()
                        detectTapGestures { onChange(((it.x - grip / 2f) / (size.width - grip)).coerceIn(0f, 1f)) }
                    }
                    .pointerInput(Unit) {
                        val grip = 36.dp.toPx()
                        detectHorizontalDragGestures { change, _ ->
                            change.consume()
                            onChange(((change.position.x - grip / 2f) / (size.width - grip)).coerceIn(0f, 1f))
                        }
                    },
            ) {
                val trackH = 40.dp.toPx()
                val top = (size.height - trackH) / 2f
                val radius = CornerRadius(12.dp.toPx())
                drawRoundRect(Rail, Offset(0f, top), Size(size.width, trackH), radius)

                val grip = 36.dp.toPx()
                val gripX = value.coerceIn(0f, 1f) * (size.width - grip)
                val fillW = gripX + grip
                val gradient = Brush.horizontalGradient(
                    0f to spectrumColor(0f), .5f to spectrumColor(.5f), 1f to spectrumColor(1f),
                    startX = 0f, endX = size.width,
                )
                drawRoundRect(gradient, Offset(0f, top), Size(fillW, trackH), radius)

                // grip
                drawRoundRect(Color.White, Offset(gripX, top), Size(grip, trackH), radius)
                drawRoundRect(
                    spectrumColor(value), Offset(gripX, top), Size(grip, trackH), radius,
                    style = Stroke(width = 2.dp.toPx()),
                )
                val dot = Color(0xFFB9BDB0)
                val cx = gripX + grip / 2f
                for (dx in listOf(-3f, 3f)) for (dy in listOf(-7f, 0f, 7f)) {
                    drawCircle(dot, 1.6.dp.toPx(), Offset(cx + dx.dp.toPx(), top + trackH / 2f + dy.dp.toPx()))
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(lowLabel, style = MaterialTheme.typography.bodySmall, color = InkSoft)
                Text(highLabel, style = MaterialTheme.typography.bodySmall, color = InkSoft)
            }
        }
    }
}
