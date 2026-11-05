package dev.imhamid.moodlink.ui.home

import dev.imhamid.moodlink.R
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.components.GlassCard
import dev.imhamid.moodlink.ui.components.MoodFace
import dev.imhamid.moodlink.ui.theme.InkSoft
import kotlinx.coroutines.delay

/** Capsule bars: how many of you (me + friends) feel each mood right now. Real data. */
@Composable
fun MoodMix(moods: List<Mood>, modifier: Modifier = Modifier) {
    val counts = moods.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(5)
    if (counts.isEmpty()) return
    val max = counts.maxOf { it.value }
    GlassCard(modifier.fillMaxWidth()) {
        Text(stringResource(R.string.mix_title), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.mix_subtitle), style = MaterialTheme.typography.bodyMedium, color = InkSoft)
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
            counts.forEachIndexed { i, (mood, n) ->
                var grown by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { delay(160L + i * 110L); grown = true }
                // Bars rise on first show and spring to a new height whenever the counts change.
                val height by animateDpAsState(
                    if (grown) (64 + 90f * n / max).dp else 30.dp,
                    spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
                    label = "bar",
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.width(54.dp).height(height)
                            .clip(RoundedCornerShape(27.dp))
                            .background(Brush.verticalGradient(listOf(mood.tint, mood.tint.copy(alpha = .35f)))),
                        contentAlignment = Alignment.TopCenter,
                    ) { MoodFace(mood, Modifier.padding(top = 8.dp).size(38.dp)) }
                    Spacer(Modifier.height(6.dp))
                    Text(stringResource(mood.labelRes), style = MaterialTheme.typography.bodyMedium, color = InkSoft)
                }
            }
        }
    }
}
