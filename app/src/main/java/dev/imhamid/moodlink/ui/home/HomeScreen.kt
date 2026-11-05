package dev.imhamid.moodlink.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.imhamid.moodlink.R
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.components.MoodFace
import dev.imhamid.moodlink.ui.components.Panel
import dev.imhamid.moodlink.ui.components.PrimaryButton
import dev.imhamid.moodlink.ui.components.SecondaryButton
import dev.imhamid.moodlink.ui.components.formatNumber
import dev.imhamid.moodlink.ui.components.pressable
import dev.imhamid.moodlink.ui.connect.ConnectCard
import dev.imhamid.moodlink.ui.mood.MoodPickerSheet
import dev.imhamid.moodlink.ui.profile.ProfileScreen
import dev.imhamid.moodlink.ui.theme.Accent
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft
import dev.imhamid.moodlink.ui.theme.Line
import dev.imhamid.moodlink.ui.theme.Paper
import dev.imhamid.moodlink.ui.theme.PanelShape
import dev.imhamid.moodlink.ui.theme.Raised

private val QuickMoods = listOf(Mood.HAPPY, Mood.LOVE, Mood.SAD, Mood.ANGRY, Mood.TIRED)

@Composable
fun HomeScreen(vm: HomeViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    var picker by remember { mutableStateOf(false) }
    var preselect by remember { mutableStateOf<Mood?>(null) }
    var profile by remember { mutableStateOf(false) }
    val open: (Mood?) -> Unit = { preselect = it; picker = true }
    val name = s.me?.displayName.orEmpty()
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    BackHandler(enabled = profile) { profile = false }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize().systemBarsPadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { TopBar(name, onProfile = { profile = true }) }
            item { MyMood(name, s.me?.mood, onPick = open) }

            if (s.requests.isNotEmpty()) {
                item { SectionTitle(stringResource(R.string.home_requests)) }
                items(s.requests, key = { it.connectionId }) { r ->
                    Panel(Modifier.fillMaxWidth()) {
                        Text(
                            stringResource(R.string.home_request_text, r.from.displayName),
                            style = MaterialTheme.typography.titleMedium, color = Ink,
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            PrimaryButton(stringResource(R.string.action_accept), { vm.accept(r.connectionId) }, Modifier.weight(1f))
                            SecondaryButton(stringResource(R.string.action_decline), { vm.decline(r.connectionId) }, Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SectionTitle(stringResource(R.string.home_people), Modifier.weight(1f))
                    if (s.friends.isNotEmpty()) Text(formatNumber(s.friends.size), color = InkSoft, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (s.friends.isEmpty()) item {
                Panel(Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.home_people_empty), color = InkSoft, style = MaterialTheme.typography.bodyMedium)
                }
            }
            items(s.friends, key = { it.connectionId }) { f -> FriendRow(f) }

            item { MoodMix(listOfNotNull(s.me?.mood) + s.friends.mapNotNull { it.profile.mood }) }
            item { ConnectCard(myCode = s.me?.inviteCode.orEmpty()) }
        }

        AnimatedVisibility(
            visible = profile,
            enter = slideInHorizontally(tween(240)) { if (rtl) -it else it } + fadeIn(tween(240)),
            exit = slideOutHorizontally(tween(200)) { if (rtl) -it else it } + fadeOut(tween(200)),
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
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.titleLarge, color = Ink, modifier = modifier)
}

@Composable
private fun TopBar(name: String, onProfile: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.app_name), color = Ink,
            style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f),
        )
        Box(
            Modifier.pressable(onClick = onProfile).size(48.dp).clip(CircleShape).background(Ink),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                name.firstOrNull()?.uppercase() ?: stringResource(R.string.profile_you).take(1),
                color = Paper, style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

/** The block the app is about: how I feel now, five quick picks, and the button to change it. */
@Composable
private fun MyMood(name: String, mood: Mood?, onPick: (Mood?) -> Unit) {
    Panel(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    if (name.isBlank()) stringResource(R.string.home_hello_anon) else stringResource(R.string.home_hello, name),
                    color = InkSoft, style = MaterialTheme.typography.bodyMedium,
                )
                Text(stringResource(R.string.home_question), color = Ink, style = MaterialTheme.typography.headlineMedium)
            }
            Crossfade(mood, animationSpec = tween(180), label = "myFace") { m ->
                MoodFace(m, Modifier.size(72.dp), squircle = true, animated = true)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickMoods.forEach { m -> QuickMood(m, selected = mood == m, onClick = { onPick(m) }, Modifier.weight(1f)) }
        }
        Spacer(Modifier.height(16.dp))
        mood?.let {
            Text(
                stringResource(R.string.home_sharing, stringResource(it.labelRes)),
                style = MaterialTheme.typography.bodyMedium, color = InkSoft,
            )
            Spacer(Modifier.height(12.dp))
        }
        PrimaryButton(
            stringResource(if (mood == null) R.string.home_share_mood else R.string.home_update_mood),
            onClick = { onPick(null) },
            container = Accent, content = Color.White,
        )
    }
}

@Composable
private fun QuickMood(mood: Mood, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier.pressable(onClick = onClick).height(56.dp).clip(shape)
            .background(Raised).border(if (selected) 2.dp else 1.dp, if (selected) Ink else Line, shape)
            .padding(6.dp),
        contentAlignment = Alignment.Center,
    ) { MoodFace(mood, Modifier.fillMaxSize(), squircle = true) }
}
