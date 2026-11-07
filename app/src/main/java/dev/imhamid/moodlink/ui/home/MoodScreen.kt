package dev.imhamid.moodlink.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.R
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.components.FuzzBall
import dev.imhamid.moodlink.ui.components.MoodSlider
import dev.imhamid.moodlink.ui.components.PrimaryButton
import dev.imhamid.moodlink.ui.components.formatNumber
import dev.imhamid.moodlink.ui.components.pressable
import dev.imhamid.moodlink.ui.components.spectrumColor
import dev.imhamid.moodlink.ui.theme.Accent
import dev.imhamid.moodlink.ui.theme.ControlShape
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.Line
import dev.imhamid.moodlink.ui.theme.Paper
import dev.imhamid.moodlink.ui.theme.Raised
import kotlinx.coroutines.delay

/** The whole point of the app on one screen: drag to how you feel, then share it. */
@Composable
fun MoodScreen(
    name: String,
    currentMood: Mood?,
    requestCount: Int,
    onShare: (Mood) -> Unit,
    onFriends: () -> Unit,
    onProfile: () -> Unit,
) {
    var position by remember { mutableFloatStateOf(Mood.positionOf(currentMood ?: Mood.CALM)) }
    var touched by remember { mutableStateOf(false) }
    var shared by remember { mutableStateOf(false) }

    // The saved mood arrives after the first frame; follow it until the person starts dragging.
    LaunchedEffect(currentMood) { if (!touched && currentMood != null) position = Mood.positionOf(currentMood) }
    LaunchedEffect(shared) { if (shared) { delay(1600); shared = false } }

    val mood = Mood.atPosition(position)
    val tint = spectrumColor(position)

    Box(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(0f to tint.copy(alpha = .30f), .65f to Paper, 1f to Paper)),
    ) {
        Column(
            Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Profile on the leading edge, Friends on the trailing edge, in both directions.
                Box(
                    Modifier.pressable(onClick = onProfile).size(48.dp).clip(CircleShape).background(Ink),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        name.firstOrNull()?.uppercase() ?: stringResource(R.string.profile_you).take(1),
                        color = Paper, style = MaterialTheme.typography.titleMedium,
                    )
                }
                FriendsButton(requestCount, onFriends)
            }

            Text(
                stringResource(R.string.picker_title), color = Ink, textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 12.dp),
            )

            FuzzBall(position, Modifier.weight(1f).fillMaxWidth())

            Crossfade(mood, animationSpec = tween(150), label = "moodName") { m ->
                Text(
                    stringResource(m.labelRes), color = Ink, textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayMedium, modifier = Modifier.fillMaxWidth(),
                )
            }

            MoodSlider(
                value = position,
                onValueChange = { position = it; touched = true; shared = false },
                lowLabel = stringResource(R.string.slider_low),
                highLabel = stringResource(R.string.slider_high),
                stateText = stringResource(mood.labelRes),
                modifier = Modifier.padding(top = 16.dp, bottom = 20.dp),
            )

            PrimaryButton(
                stringResource(if (shared) R.string.shared_done else R.string.picker_share),
                onClick = { onShare(mood); shared = true },
            )
        }
    }
}

@Composable
private fun FriendsButton(requestCount: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.pressable(onClick = onClick).defaultMinSize(minHeight = 48.dp).clip(ControlShape)
            .background(Raised).border(1.dp, Line, ControlShape).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(stringResource(R.string.friends_button), color = Ink, style = MaterialTheme.typography.labelLarge)
        if (requestCount > 0) Box(
            Modifier.defaultMinSize(minWidth = 22.dp, minHeight = 22.dp).clip(CircleShape).background(Accent),
            contentAlignment = Alignment.Center,
        ) { Text(formatNumber(requestCount), color = Color.White, style = MaterialTheme.typography.bodySmall) }
    }
}
