package dev.imhamid.moodlink.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.theme.Ink

/** Big pastel tile with the mood's character, like the "How are you feeling" grid. */
@Composable
fun MoodTile(mood: Mood, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scale by animateFloatAsState(if (selected) 1.04f else 1f, spring(dampingRatio = .55f), label = "tile")
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier
            .height(132.dp)
            .scale(scale)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(mood.tint, mood.tint.copy(alpha = .55f))))
            .then(if (selected) Modifier.border(3.dp, mood.tintDark, shape) else Modifier)
            .clickable(onClick = onClick),
    ) {
        Text(
            mood.label, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
        )
        MoodFace(
            mood,
            Modifier.align(Alignment.BottomStart).padding(start = 14.dp, bottom = 10.dp).size(58.dp).rotate(-10f),
            squircle = true,
        )
        Box(
            Modifier.align(Alignment.BottomEnd).padding(12.dp).size(34.dp)
                .clip(CircleShape).background(Color.White.copy(alpha = .55f)),
            contentAlignment = Alignment.Center,
        ) { Text(if (selected) "✓" else "↗", color = Ink, fontSize = 16.sp) }
    }
}
