package dev.imhamid.moodlink.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.data.model.Friend
import dev.imhamid.moodlink.ui.components.GlassCard
import dev.imhamid.moodlink.ui.components.MoodBubble

@Composable
fun FriendCard(friend: Friend, modifier: Modifier = Modifier) {
    val p = friend.profile
    GlassCard(modifier.fillMaxWidth(), container = (p.mood?.tint ?: androidx.compose.ui.graphics.Color.White).copy(alpha = .55f)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            MoodBubble(p.mood, 60.dp)
            Column(Modifier.weight(1f)) {
                Text(p.displayName, style = MaterialTheme.typography.titleMedium)
                Text(
                    p.mood?.let { "is feeling ${it.label.lowercase()}" } ?: "hasn't shared a mood yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (p.note.isNotBlank()) Text("“${p.note}”", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
