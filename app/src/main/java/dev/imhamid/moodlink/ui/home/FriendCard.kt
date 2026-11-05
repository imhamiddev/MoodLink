package dev.imhamid.moodlink.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.data.model.Friend
import dev.imhamid.moodlink.ui.components.MoodFace
import dev.imhamid.moodlink.ui.components.bounceClick
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft
import dev.imhamid.moodlink.ui.theme.Lavender200
import kotlinx.coroutines.launch

/** One person in "Your people". Tap the card and their character wiggles hello. */
@Composable
fun FriendTile(friend: Friend, modifier: Modifier = Modifier) {
    val p = friend.profile
    val tint = p.mood?.tint ?: Lavender200
    val scope = rememberCoroutineScope()
    val rot = remember { Animatable(0f) }
    val pop = remember { Animatable(1f) }

    Box(
        modifier
            .bounceClick(scaleDown = .96f) {
                scope.launch {
                    launch { pop.animateTo(1.18f, tween(120)); pop.animateTo(1f, spring(Spring.DampingRatioMediumBouncy)) }
                    rot.animateTo(-16f, tween(90)); rot.animateTo(14f, tween(110))
                    rot.animateTo(-7f, tween(90)); rot.animateTo(0f, spring(Spring.DampingRatioMediumBouncy))
                }
            }
            .height(196.dp).clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(tint, tint.copy(alpha = .5f)))),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                p.displayName.ifBlank { "Friend" }, style = MaterialTheme.typography.titleLarge,
                color = Ink, maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
            Crossfade(p.mood, label = "friendMood") { m ->
                Text(
                    m?.let { "feeling ${it.label.lowercase()}" } ?: "no mood yet",
                    style = MaterialTheme.typography.bodyMedium, color = InkSoft,
                )
            }
        }
        // When their mood changes the character swaps with a pop.
        AnimatedContent(
            targetState = p.mood,
            transitionSpec = {
                (androidx.compose.animation.scaleIn(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow), initialScale = .4f) +
                    androidx.compose.animation.fadeIn()) togetherWith androidx.compose.animation.fadeOut(tween(150))
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
            label = "friendFace",
        ) { m ->
            MoodFace(
                m,
                Modifier.size(84.dp).graphicsLayer { rotationZ = rot.value; scaleX = pop.value; scaleY = pop.value },
                squircle = true, animated = true,
            )
        }
    }
}
