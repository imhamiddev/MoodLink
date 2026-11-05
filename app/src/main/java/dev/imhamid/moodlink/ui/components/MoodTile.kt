package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.Line
import dev.imhamid.moodlink.ui.theme.PanelShape
import dev.imhamid.moodlink.ui.theme.Raised

/** One mood in the picker: its face and its name on paper. Selected gets a 2dp ink border. */
@Composable
fun MoodTile(mood: Mood, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .pressable(onClick = onClick)
            .defaultMinSize(minHeight = 72.dp)
            .clip(PanelShape)
            .background(Raised)
            .border(if (selected) 2.dp else 1.dp, if (selected) Ink else Line, PanelShape)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MoodFace(mood, Modifier.size(48.dp), squircle = true, animated = selected)
        Text(stringResource(mood.labelRes), color = Ink, style = MaterialTheme.typography.titleMedium)
    }
}
