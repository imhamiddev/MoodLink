package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.ui.theme.ControlShape
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.Line
import dev.imhamid.moodlink.ui.theme.Paper

/** The main action on a screen: solid ink, full width. One per screen. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    container: Color = Ink,
    content: Color = Paper,
) {
    Box(
        modifier
            .pressable(enabled = enabled && !loading, onClick = onClick)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .alpha(if (enabled) 1f else .4f)
            .clip(ControlShape)
            .background(container)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) CircularProgressIndicator(Modifier.size(22.dp), color = content, strokeWidth = 2.dp)
        else Text(text, color = content, style = MaterialTheme.typography.labelLarge)
    }
}

/** A quieter action next to a primary one: transparent with a line around it. */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Ink,
    border: Color = Line,
    fillWidth: Boolean = true,
) {
    Box(
        modifier
            .pressable(enabled = enabled, onClick = onClick)
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .defaultMinSize(minHeight = 48.dp)
            .alpha(if (enabled) 1f else .4f)
            .clip(ControlShape)
            .border(BorderStroke(1.dp, border), ControlShape)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) { Text(text, color = color, style = MaterialTheme.typography.labelLarge) }
}
