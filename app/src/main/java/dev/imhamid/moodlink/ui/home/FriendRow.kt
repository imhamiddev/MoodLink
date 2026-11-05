package dev.imhamid.moodlink.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.R
import dev.imhamid.moodlink.data.model.Friend
import dev.imhamid.moodlink.ui.components.MoodFace
import dev.imhamid.moodlink.ui.components.Panel
import dev.imhamid.moodlink.ui.components.relativeTime
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft

/** One person in the list: their face, their name, the mood and when it was set. */
@Composable
fun FriendRow(friend: Friend, modifier: Modifier = Modifier) {
    val p = friend.profile
    val updated = relativeTime(p.moodUpdatedAt)
    Panel(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            MoodFace(p.mood, Modifier.size(56.dp), squircle = true)
            Column(Modifier.weight(1f)) {
                Text(
                    p.displayName.ifBlank { stringResource(R.string.friend_default_name) },
                    style = MaterialTheme.typography.titleMedium, color = Ink,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                Text(
                    stringResource(p.mood?.labelRes ?: R.string.friend_no_mood),
                    style = MaterialTheme.typography.bodyMedium, color = InkSoft,
                )
            }
            if (p.mood != null && updated != null) {
                Text(updated, style = MaterialTheme.typography.bodySmall, color = InkSoft)
            }
        }
    }
}
