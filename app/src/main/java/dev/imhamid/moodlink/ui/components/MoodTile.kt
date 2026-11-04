package dev.imhamid.moodlink.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.theme.Ink

/** Pastel tile with a big emoji, like the "How are you feeling today?" grid. */
@Composable
fun MoodTile(mood: Mood, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scale by animateFloatAsState(if (selected) 1.05f else 1f, spring(dampingRatio = .5f), label = "tile")
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier
            .scale(scale)
            .clip(shape)
            .background(mood.tint)
            .then(if (selected) Modifier.border(BorderStroke(3.dp, mood.tintDark), shape) else Modifier)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(mood.emoji, fontSize = 38.sp)
        Text(mood.label, color = Ink, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
    }
}
