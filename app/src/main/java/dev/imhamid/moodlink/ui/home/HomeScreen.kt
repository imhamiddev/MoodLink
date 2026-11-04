package dev.imhamid.moodlink.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.imhamid.moodlink.ui.components.*
import dev.imhamid.moodlink.ui.connect.ConnectCard
import dev.imhamid.moodlink.ui.mood.MoodPickerSheet

@Composable
fun HomeScreen(vm: HomeViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    var picker by remember { mutableStateOf(false) }

    LazyColumn(
        Modifier.fillMaxSize().systemBarsPadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Hello, ${s.me?.displayName.orEmpty()}!", style = MaterialTheme.typography.bodyLarge)
                    Text("How are you today?", style = MaterialTheme.typography.headlineMedium)
                }
                TextButton(onClick = vm::signOut) { Text("Log out") }
            }
        }
        item {
            GlassCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    MoodBubble(s.me?.mood, 64.dp)
                    Column(Modifier.weight(1f)) {
                        Text(s.me?.mood?.label ?: "No mood yet", style = MaterialTheme.typography.titleLarge)
                        Text("Tap to update", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(12.dp))
                GradientButton("Share my mood", onClick = { picker = true })
            }
        }
        if (s.requests.isNotEmpty()) {
            item { Text("Requests", style = MaterialTheme.typography.titleLarge) }
            items(s.requests, key = { it.connectionId }) { r ->
                GlassCard(Modifier.fillMaxWidth()) {
                    Text("${r.from.displayName} wants to connect", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { vm.accept(r.connectionId) }) { Text("Accept") }
                        TextButton(onClick = { vm.decline(r.connectionId) }) { Text("Decline") }
                    }
                }
            }
        }
        item { Text("Your people", style = MaterialTheme.typography.titleLarge) }
        if (s.friends.isEmpty()) item {
            Text("No one yet — share your code below to get started 💌",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(s.friends, key = { it.connectionId }) { FriendCard(it) }
        item { ConnectCard(myCode = s.me?.inviteCode.orEmpty()) }
    }

    if (picker) MoodPickerSheet(
        current = s.me?.mood,
        onDismiss = { picker = false },
        onShare = { m, n -> vm.shareMood(m, n); picker = false },
    )
}
