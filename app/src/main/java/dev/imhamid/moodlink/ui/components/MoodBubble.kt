package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.data.model.Mood

@Composable
fun MoodBubble(mood: Mood?, size: Dp = 56.dp, modifier: Modifier = Modifier) {
    MoodFace(mood, modifier.size(size))
}
