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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.data.model.Friend
import dev.imhamid.moodlink.ui.components.MoodFace
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft
import dev.imhamid.moodlink.ui.theme.Lavender200

/** One person in "Your people": pastel card with their name, mood and character. */
@Composable
fun FriendTile(friend: Friend, modifier: Modifier = Modifier) {
    val p = friend.profile
    val tint = p.mood?.tint ?: Lavender200
    Box(
        modifier.height(196.dp).clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(tint, tint.copy(alpha = .5f)))),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                p.displayName.ifBlank { "Friend" }, style = MaterialTheme.typography.titleLarge,
                color = Ink, maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
            Text(
                p.mood?.let { "feeling ${it.label.lowercase()}" } ?: "no mood yet",
                style = MaterialTheme.typography.bodyMedium, color = InkSoft,
            )
            if (p.note.isNotBlank()) Text(
                "“${p.note}”", style = MaterialTheme.typography.bodyMedium,
                color = Ink, maxLines = 2, overflow = TextOverflow.Ellipsis,
            )
        }
        MoodFace(p.mood, Modifier.align(Alignment.BottomEnd).padding(12.dp).size(84.dp), squircle = true)
    }
}
