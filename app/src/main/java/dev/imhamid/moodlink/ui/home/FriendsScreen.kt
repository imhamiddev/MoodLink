package dev.imhamid.moodlink.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.R
import dev.imhamid.moodlink.ui.components.Panel
import dev.imhamid.moodlink.ui.components.PrimaryButton
import dev.imhamid.moodlink.ui.components.SecondaryButton
import dev.imhamid.moodlink.ui.components.formatNumber
import dev.imhamid.moodlink.ui.components.pressable
import dev.imhamid.moodlink.ui.connect.ConnectCard
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft
import dev.imhamid.moodlink.ui.theme.Paper

/** Everyone else: connection requests, the people list, the mood mix and the invite code. */
@Composable
fun FriendsScreen(
    state: HomeState,
    onBack: () -> Unit,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxSize().background(Paper).systemBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.pressable(onClick = onBack).size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.back), tint = Ink)
                }
                Text(stringResource(R.string.friends_title), style = MaterialTheme.typography.titleLarge, color = Ink, modifier = Modifier.weight(1f))
                if (state.friends.isNotEmpty()) Text(formatNumber(state.friends.size), color = InkSoft, style = MaterialTheme.typography.bodyMedium)
            }
        }

        if (state.requests.isNotEmpty()) {
            item { Text(stringResource(R.string.home_requests), style = MaterialTheme.typography.titleMedium, color = Ink) }
            items(state.requests, key = { it.connectionId }) { r ->
                Panel(Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(R.string.home_request_text, r.from.displayName),
                        style = MaterialTheme.typography.titleMedium, color = Ink,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PrimaryButton(stringResource(R.string.action_accept), { onAccept(r.connectionId) }, Modifier.weight(1f))
                        SecondaryButton(stringResource(R.string.action_decline), { onDecline(r.connectionId) }, Modifier.weight(1f))
                    }
                }
            }
        }

        if (state.friends.isEmpty()) item {
            Panel(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_people_empty), color = InkSoft, style = MaterialTheme.typography.bodyMedium)
            }
        }
        items(state.friends, key = { it.connectionId }) { f -> FriendRow(f) }

        item { MoodMix(listOfNotNull(state.me?.mood) + state.friends.mapNotNull { it.profile.mood }) }
        item { ConnectCard(myCode = state.me?.inviteCode.orEmpty()) }
    }
}
