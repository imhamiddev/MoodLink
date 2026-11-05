package dev.imhamid.moodlink.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.R
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.components.Panel
import dev.imhamid.moodlink.ui.components.formatNumber
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft

/** One bar split by mood, then a legend. Counts are real: me plus everyone I am connected with. */
@Composable
fun MoodMix(moods: List<Mood>, modifier: Modifier = Modifier) {
    val counts = moods.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }
    if (counts.isEmpty()) return
    Panel(modifier.fillMaxWidth()) {
        Text(stringResource(R.string.mix_title), style = MaterialTheme.typography.titleMedium, color = Ink)
        Text(stringResource(R.string.mix_subtitle), style = MaterialTheme.typography.bodyMedium, color = InkSoft)
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(4.dp)), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            counts.forEach { (mood, n) -> Box(Modifier.weight(n.toFloat()).height(16.dp).background(mood.tint)) }
        }
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            counts.forEach { (mood, n) ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(mood.tint))
                    Text(stringResource(mood.labelRes), color = Ink, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text(formatNumber(n), color = InkSoft, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
