package dev.imhamid.moodlink.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.components.*
import dev.imhamid.moodlink.ui.connect.ConnectCard
import dev.imhamid.moodlink.ui.mood.MoodPickerSheet
import dev.imhamid.moodlink.ui.profile.ProfileScreen
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
    var profile by remember { mutableStateOf(false) }
    val open: (Mood?) -> Unit = { preselect = it; picker = true }
    val name = s.me?.displayName.orEmpty()

    BackHandler(enabled = profile) { profile = false }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize().systemBarsPadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { TopBar(name, s.me?.mood, onProfile = { profile = true }, onMood = { open(null) }, Modifier.entrance(0)) }
            item { GreetingCard(name, s.me?.mood, onPick = open, Modifier.entrance(1)) }

            if (s.requests.isNotEmpty()) {
                item { Text("Requests", style = MaterialTheme.typography.titleLarge, modifier = Modifier.animateItem()) }
                items(s.requests, key = { it.connectionId }) { r ->
                    GlassCard(Modifier.fillMaxWidth().animateItem()) {
                        Text("${r.from.displayName} wants to connect", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.bounceClick { vm.accept(r.connectionId) }
                                    .clip(CircleShape).background(CtaGradient)
                                    .padding(horizontal = 22.dp, vertical = 10.dp),
                            ) { Text("Accept", color = Color.White, fontWeight = FontWeight.SemiBold) }
                            TextButton(onClick = { vm.decline(r.connectionId) }) { Text("Decline", color = InkSoft) }
                        }
                    }
                }
            }

            item {
                Row(Modifier.entrance(2), verticalAlignment = Alignment.CenterVertically) {
                    Text("Your people", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    AnimatedContent(s.friends.size, label = "count") { n -> if (n > 0) Text("$n", color = InkSoft) else Spacer(Modifier) }
                }
            }
            if (s.friends.isEmpty()) item {
                GlassCard(Modifier.fillMaxWidth().entrance(3)) {
                    Text(
                        "No one yet. Share your code below to invite someone you love 💌",
                        color = InkSoft, style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            itemsIndexed(s.friends.chunked(2), key = { _, pair -> pair.first().connectionId }) { i, pair ->
                Row(Modifier.fillMaxWidth().animateItem(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    pair.forEachIndexed { j, f -> FriendTile(f, Modifier.weight(1f).entrance(3 + i * 2 + j)) }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            item { MoodMix(listOfNotNull(s.me?.mood) + s.friends.mapNotNull { it.profile.mood }, Modifier.entrance(4)) }
            item { ConnectCard(myCode = s.me?.inviteCode.orEmpty()) }
        }

        // Profile slides over the home screen from the right.
        AnimatedVisibility(
            visible = profile,
            enter = slideInHorizontally(spring(dampingRatio = .85f, stiffness = Spring.StiffnessMediumLow)) { it } + fadeIn(),
            exit = slideOutHorizontally(tween(260)) { it } + fadeOut(tween(200)),
        ) {
            ProfileScreen(state = s, onBack = { profile = false }, onLogout = vm::signOut)
        }
    }

    if (picker) MoodPickerSheet(
        current = preselect ?: s.me?.mood,
        onDismiss = { picker = false },
        onShare = { m -> vm.shareMood(m); picker = false },
    )
}

@Composable
private fun TopBar(name: String, mood: Mood?, onProfile: () -> Unit, onMood: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.bounceClick(onClick = onProfile).size(48.dp).clip(CircleShape).background(CtaGradient),
            contentAlignment = Alignment.Center,
        ) {
            Crossfade(name.firstOrNull()?.uppercase() ?: "•", label = "initial") {
                Text(it, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.bounceClick(onClick = onMood).clip(CircleShape).background(Color.White.copy(alpha = .78f))
                .padding(start = 8.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnimatedContent(
                mood,
                transitionSpec = {
                    (androidx.compose.animation.scaleIn(spring(Spring.DampingRatioMediumBouncy), initialScale = .3f) + fadeIn()) togetherWith
                        androidx.compose.animation.scaleOut(targetScale = .3f) + fadeOut(tween(120))
                },
                label = "chipFace",
            ) { m -> MoodFace(m, Modifier.size(32.dp), animated = true) }
            Crossfade(mood?.label ?: "Set mood", label = "chipLabel") {
                Text(it, color = Ink, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/** "Hello, Name! How are you today?" with the quick-pick mood circles. */
@Composable
private fun GreetingCard(
    name: String, mood: Mood?, onPick: (Mood?) -> Unit, modifier: Modifier = Modifier,
) {
    GlassCard(modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.floating(3f, 3000).clip(CircleShape).background(Color.White.copy(alpha = .7f)).padding(horizontal = 20.dp, vertical = 6.dp)) {
                Text("Hello, ${name.ifBlank { "there" }}!", color = InkSoft, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "How are you today?", style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center, color = Ink,
            )
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                QuickMoods.forEachIndexed { i, m -> QuickMood(m, selected = mood == m, index = i, onClick = { onPick(m) }) }
            }
            Spacer(Modifier.height(16.dp))
            AnimatedVisibility(
                visible = mood != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Crossfade(mood, label = "sharing") { m ->
                        Text(
                            "You're sharing: ${m?.label.orEmpty()}",
                            style = MaterialTheme.typography.bodyMedium, color = InkSoft, textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
            GradientButton(if (mood == null) "Share my mood" else "Update my mood", onClick = { onPick(null) })
        }
    }
}

@Composable
private fun QuickMood(mood: Mood, selected: Boolean, index: Int, onClick: () -> Unit) {
    val ring by animateColorAsState(if (selected) Purple500 else Color.Transparent, tween(250), label = "ring")
    val pop by animateFloatAsState(
        if (selected) 1.15f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "pop",
    )
    Box(
        Modifier.bounceClick(scaleDown = .88f, onClick = onClick)
            .entrance(index, key = mood)
            .graphicsLayer { scaleX = pop; scaleY = pop }
            .size(54.dp).clip(CircleShape).background(Color.White.copy(alpha = .75f))
            .border(3.dp, ring, CircleShape).padding(6.dp),
        contentAlignment = Alignment.Center,
    ) { MoodFace(mood, Modifier.fillMaxSize(), animated = selected) }
}
