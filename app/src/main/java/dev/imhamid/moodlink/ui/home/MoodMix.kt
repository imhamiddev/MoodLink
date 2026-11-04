package dev.imhamid.moodlink.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.components.GlassCard
import dev.imhamid.moodlink.ui.components.MoodFace
import dev.imhamid.moodlink.ui.theme.InkSoft

/** Capsule bars: how many of you (me + friends) feel each mood right now. Real data. */
@Composable
fun MoodMix(moods: List<Mood>, modifier: Modifier = Modifier) {
    val counts = moods.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(5)
    if (counts.isEmpty()) return
    val max = counts.maxOf { it.value }
    GlassCard(modifier.fillMaxWidth()) {
        Text("Mood mix", style = MaterialTheme.typography.titleMedium)
        Text("How everyone feels right now", style = MaterialTheme.typography.bodyMedium, color = InkSoft)
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
            counts.forEach { (mood, n) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.width(54.dp).height((64 + 90f * n / max).dp)
                            .clip(RoundedCornerShape(27.dp))
                            .background(Brush.verticalGradient(listOf(mood.tint, mood.tint.copy(alpha = .35f)))),
                        contentAlignment = Alignment.TopCenter,
                    ) { MoodFace(mood, Modifier.padding(top = 8.dp).size(38.dp)) }
                    Spacer(Modifier.height(6.dp))
                    Text(mood.label, style = MaterialTheme.typography.bodyMedium, color = InkSoft)
                }
            }
        }
    }
}
