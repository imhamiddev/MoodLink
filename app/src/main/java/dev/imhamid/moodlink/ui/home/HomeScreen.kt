package dev.imhamid.moodlink.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.components.*
import dev.imhamid.moodlink.ui.connect.ConnectCard
import dev.imhamid.moodlink.ui.mood.MoodPickerSheet
import dev.imhamid.moodlink.ui.theme.CtaGradient
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft
import dev.imhamid.moodlink.ui.theme.Purple500

private val QuickMoods = listOf(Mood.HAPPY, Mood.LOVE, Mood.SAD, Mood.ANGRY, Mood.TIRED)

@Composable
fun HomeScreen(vm: HomeViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    var picker by remember { mutableStateOf(false) }
    var preselect by remember { mutableStateOf<Mood?>(null) }
    val open: (Mood?) -> Unit = { preselect = it; picker = true }
    val name = s.me?.displayName.orEmpty()

    LazyColumn(
        Modifier.fillMaxSize().systemBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { TopBar(name, onLogout = vm::signOut) }
        item { GreetingCard(name, s.me?.mood, s.me?.note.orEmpty(), onPick = open) }

        if (s.requests.isNotEmpty()) {
            item { Text("Requests", style = MaterialTheme.typography.titleLarge) }
            items(s.requests, key = { it.connectionId }) { r ->
                GlassCard(Modifier.fillMaxWidth()) {
                    Text("${r.from.displayName} wants to connect", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.clip(CircleShape).background(CtaGradient)
                                .clickable { vm.accept(r.connectionId) }.padding(horizontal = 22.dp, vertical = 10.dp),
                        ) { Text("Accept", color = Color.White, fontWeight = FontWeight.SemiBold) }
                        TextButton(onClick = { vm.decline(r.connectionId) }) { Text("Decline", color = InkSoft) }
                    }
                }
            }
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Your people", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                if (s.friends.isNotEmpty()) Text("${s.friends.size}", color = InkSoft)
            }
        }
        if (s.friends.isEmpty()) item {
            GlassCard(Modifier.fillMaxWidth()) {
                Text(
                    "No one yet. Share your code below to invite someone you love 💌",
                    color = InkSoft, style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        items(s.friends.chunked(2), key = { it.first().connectionId }) { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { FriendTile(it, Modifier.weight(1f)) }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        item { MoodMix(listOfNotNull(s.me?.mood) + s.friends.mapNotNull { it.profile.mood }) }
        item { ConnectCard(myCode = s.me?.inviteCode.orEmpty()) }
    }

    if (picker) MoodPickerSheet(
        current = preselect ?: s.me?.mood,
        onDismiss = { picker = false },
        onShare = { m, n -> vm.shareMood(m, n); picker = false },
    )
}

@Composable
private fun TopBar(name: String, onLogout: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(CtaGradient),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                name.firstOrNull()?.uppercase() ?: "•", color = Color.White,
                fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium,
            )
        }
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = .75f))
                .clickable(onClick = onLogout),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Log out", tint = Ink) }
    }
}

/** "Hello, Name! How are you today?" with the quick-pick mood circles. */
@Composable
private fun GreetingCard(name: String, mood: Mood?, note: String, onPick: (Mood?) -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.clip(CircleShape).background(Color.White.copy(alpha = .7f)).padding(horizontal = 20.dp, vertical = 6.dp)) {
                Text("Hello, ${name.ifBlank { "there" }}!", color = InkSoft, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "How are you today?", style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center, color = Ink,
            )
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                QuickMoods.forEach { m ->
                    val sel = mood == m
                    Box(
                        Modifier.size(54.dp).clip(CircleShape).background(Color.White.copy(alpha = .75f))
                            .then(if (sel) Modifier.border(3.dp, Purple500, CircleShape) else Modifier)
                            .clickable { onPick(m) }.padding(6.dp),
                        contentAlignment = Alignment.Center,
                    ) { MoodFace(m, Modifier.fillMaxSize()) }
                }
            }
            Spacer(Modifier.height(16.dp))
            if (mood != null) {
                Text(
                    "You're sharing: ${mood.label}" + if (note.isNotBlank()) " · “$note”" else "",
                    style = MaterialTheme.typography.bodyMedium, color = InkSoft, textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
            }
            GradientButton(if (mood == null) "Share my mood" else "Update my mood", onClick = { onPick(null) })
        }
    }
}
