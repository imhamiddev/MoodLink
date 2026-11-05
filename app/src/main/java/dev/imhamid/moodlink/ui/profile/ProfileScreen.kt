package dev.imhamid.moodlink.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.imhamid.moodlink.BuildConfig
import dev.imhamid.moodlink.R
import dev.imhamid.moodlink.ui.components.LanguageSwitch
import dev.imhamid.moodlink.ui.components.MoodFace
import dev.imhamid.moodlink.ui.components.Panel
import dev.imhamid.moodlink.ui.components.SecondaryButton
import dev.imhamid.moodlink.ui.components.formatNumber
import dev.imhamid.moodlink.ui.components.pressable
import dev.imhamid.moodlink.ui.home.HomeState
import dev.imhamid.moodlink.ui.theme.Danger
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft
import dev.imhamid.moodlink.ui.theme.Line
import dev.imhamid.moodlink.ui.theme.Paper
import dev.imhamid.moodlink.ui.theme.Raised
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(state: HomeState, onBack: () -> Unit, onLogout: () -> Unit) {
    val me = state.me
    var confirm by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().background(Paper).systemBarsPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.pressable(onClick = onBack).size(48.dp), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.back), tint = Ink)
            }
            Text(stringResource(R.string.profile_title), style = MaterialTheme.typography.titleLarge, color = Ink)
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MoodFace(me?.mood, Modifier.size(88.dp), squircle = true, animated = true)
            Column(Modifier.weight(1f)) {
                Text(
                    me?.displayName.orEmpty().ifBlank { stringResource(R.string.profile_you) },
                    style = MaterialTheme.typography.headlineMedium, color = Ink,
                )
                SelectionContainer {
                    Text(me?.email.orEmpty(), style = MaterialTheme.typography.bodyMedium, color = InkSoft)
                }
            }
        }

        Panel(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth()) {
                Stat(stringResource(R.string.stat_people), formatNumber(state.friends.size), Modifier.weight(1f))
                Stat(stringResource(R.string.stat_feeling), me?.mood?.let { stringResource(it.labelRes) } ?: "-", Modifier.weight(1f))
            }
        }

        InviteRow(me?.inviteCode.orEmpty())

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium, color = Ink, modifier = Modifier.weight(1f))
            LanguageSwitch()
        }

        HorizontalDivider(color = Line)
        SecondaryButton(stringResource(R.string.profile_logout), { confirm = true }, color = Danger, border = Danger)

        Text(
            stringResource(R.string.profile_version, BuildConfig.VERSION_NAME),
            color = InkSoft, style = MaterialTheme.typography.bodySmall,
        )
    }

    if (confirm) AlertDialog(
        onDismissRequest = { confirm = false },
        containerColor = Raised,
        title = { Text(stringResource(R.string.logout_title), fontWeight = FontWeight.Bold) },
        text = { Text(stringResource(R.string.logout_body)) },
        confirmButton = {
            TextButton(onClick = { confirm = false; onLogout() }, Modifier.defaultMinSize(minHeight = 48.dp)) {
                Text(stringResource(R.string.profile_logout), color = Danger, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = { confirm = false }, Modifier.defaultMinSize(minHeight = 48.dp)) {
                Text(stringResource(R.string.logout_stay), color = InkSoft)
            }
        },
    )
}

@Composable
private fun Stat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = InkSoft)
        Text(value, style = MaterialTheme.typography.titleLarge, color = Ink, maxLines = 1)
    }
}

@Composable
private fun InviteRow(code: String, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) { if (copied) { delay(1600); copied = false } }

    Panel(modifier.fillMaxWidth()) {
        Text(stringResource(R.string.profile_invite_code), style = MaterialTheme.typography.bodySmall, color = InkSoft)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                code.ifBlank { "······" }, modifier = Modifier.weight(1f),
                fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp, color = Ink,
            )
            SecondaryButton(
                stringResource(if (copied) R.string.action_copied else R.string.action_copy),
                { clipboard.setText(AnnotatedString(code)); copied = true },
                fillWidth = false,
            )
        }
    }
}
