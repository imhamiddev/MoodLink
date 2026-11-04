package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.theme.Lavender200

@Composable
fun MoodBubble(mood: Mood?, size: Dp = 56.dp, modifier: Modifier = Modifier) {
    Box(
        modifier.size(size).clip(CircleShape).background(mood?.tint ?: Lavender200),
        contentAlignment = Alignment.Center,
    ) { Text(mood?.emoji ?: "💭", fontSize = (size.value * .5f).sp) }
}
