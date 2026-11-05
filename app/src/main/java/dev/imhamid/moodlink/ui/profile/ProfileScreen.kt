package dev.imhamid.moodlink.ui.profile

import dev.imhamid.moodlink.R
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.imhamid.moodlink.BuildConfig
import dev.imhamid.moodlink.ui.components.*
import dev.imhamid.moodlink.ui.home.HomeState
import dev.imhamid.moodlink.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(state: HomeState, onBack: () -> Unit, onLogout: () -> Unit) {
    val me = state.me
    var confirm by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().background(ScreenGradient).systemBarsPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconCircle(onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.back), tint = Ink) }
            Text(stringResource(R.string.profile_title), style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            Spacer(Modifier.size(48.dp))
        }

        Box(Modifier.entrance(0).size(150.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier.fillMaxSize().breathing(.06f, 2600).clip(CircleShape)
                    .background(Brush.radialGradient(listOf((me?.mood?.tint ?: Lavender200).copy(alpha = .9f), Color.Transparent))),
            )
            MoodFace(me?.mood, Modifier.size(112.dp).breathing(.05f, 2000), animated = true)
        }

        Column(Modifier.entrance(1), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(me?.displayName.orEmpty().ifBlank { stringResource(R.string.profile_you) }, style = MaterialTheme.typography.headlineMedium, color = Ink)
            Text(me?.email.orEmpty(), style = MaterialTheme.typography.bodyMedium, color = InkSoft)
        }

        Row(Modifier.entrance(2).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(stringResource(R.string.stat_people), state.friends.size.toString(), Modifier.weight(1f))
            StatCard(stringResource(R.string.stat_feeling), me?.mood?.let { stringResource(it.labelRes) } ?: "–", Modifier.weight(1f))
        }

        InviteRow(me?.inviteCode.orEmpty(), Modifier.entrance(3))

        Row(
            Modifier.entrance(4).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            LanguageSwitch()
        }

        Box(
            Modifier.entrance(5).fillMaxWidth().height(56.dp)
                .bounceClick { confirm = true }
                .clip(CircleShape).background(Color.White.copy(alpha = .6f))
                .border(1.5.dp, Danger.copy(alpha = .55f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = Danger)
                Text(stringResource(R.string.profile_logout), color = Danger, fontWeight = FontWeight.SemiBold)
            }
        }

        Text(stringResource(R.string.profile_version, BuildConfig.VERSION_NAME), color = InkSoft, style = MaterialTheme.typography.bodyMedium)
    }

    if (confirm) AlertDialog(
        onDismissRequest = { confirm = false },
        shape = RoundedCornerShape(28.dp),
        containerColor = Color(0xFFF8F1FF),
        title = { Text(stringResource(R.string.logout_title), fontWeight = FontWeight.Bold) },
        text = { Text(stringResource(R.string.logout_body)) },
        confirmButton = { TextButton(onClick = { confirm = false; onLogout() }) { Text(stringResource(R.string.profile_logout), color = Danger, fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text(stringResource(R.string.logout_stay), color = InkSoft) } },
    )
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    GlassCard(modifier) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = InkSoft)
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                (slideInVertically(tween(320)) { it / 2 } + fadeIn(tween(320))) togetherWith
                    (slideOutVertically(tween(220)) { -it / 2 } + fadeOut(tween(160))) using SizeTransform(clip = false)
            },
            label = "stat",
        ) { Text(it, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Ink, maxLines = 1) }
    }
}

@Composable
private fun InviteRow(code: String, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) { if (copied) { delay(1600); copied = false } }

    GlassCard(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.profile_invite_code), style = MaterialTheme.typography.bodyMedium, color = InkSoft)
                Text(code.ifBlank { "······" }, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 4.sp, color = Ink)
            }
            Box(
                Modifier.bounceClick { clipboard.setText(AnnotatedString(code)); copied = true }
                    .clip(CircleShape).background(if (copied) Color(0xFFCFF5E4) else CtaGradientSoft)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                AnimatedContent(copied, transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) }, label = "copy") {
                    Text(stringResource(if (it) R.string.action_copied else R.string.action_copy), fontWeight = FontWeight.SemiBold, color = Ink)
                }
            }
        }
    }
}

private val CtaGradientSoft = Color(0xFFE6D9FF)
