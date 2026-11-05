package dev.imhamid.moodlink.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Press feedback: a quick shrink to 97% and a light haptic tick. No ripple, no bounce. */
fun Modifier.pressable(
    enabled: Boolean = true,
    scaleDown: Float = .97f,
    onClick: () -> Unit,
): Modifier = composed {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) scaleDown else 1f, tween(90), label = "press")
    val haptic = LocalHapticFeedback.current
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(interactionSource = source, indication = null, enabled = enabled) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }
}

/** Springy press feedback + a light haptic tick. Replaces the ripple everywhere. */
fun Modifier.bounceClick(
    enabled: Boolean = true,
    scaleDown: Float = .93f,
    onClick: () -> Unit,
): Modifier = composed {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) scaleDown else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press",
    )
    val haptic = LocalHapticFeedback.current
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(interactionSource = source, indication = null, enabled = enabled) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }
}

/** Fade + rise + tiny scale-in, staggered by [index]. Plays once per item. */
fun Modifier.entrance(index: Int = 0, key: Any? = null): Modifier = composed {
    var shown by rememberSaveable(key) { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!shown) delay(index * 70L)
        shown = true
    }
    val p by animateFloatAsState(if (shown) 1f else 0f, tween(560, easing = FastOutSlowInEasing), label = "entrance")
    this.graphicsLayer {
        alpha = p
        translationY = (1f - p) * 36.dp.toPx()
        val s = .95f + .05f * p
        scaleX = s
        scaleY = s
    }
}

/** Slow, gentle "breathing" pulse for characters. */
fun Modifier.breathing(amount: Float = .04f, durationMs: Int = 2200): Modifier = composed {
    val t = rememberInfiniteTransition(label = "breath")
    val s by t.animateFloat(
        1f, 1f + amount,
        infiniteRepeatable(tween(durationMs, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b",
    )
    this.graphicsLayer { scaleX = s; scaleY = s }
}

/** Slow vertical bob, e.g. for the speech bubble. */
fun Modifier.floating(distance: Float = 6f, durationMs: Int = 2600): Modifier = composed {
    val t = rememberInfiniteTransition(label = "float")
    val y by t.animateFloat(
        -1f, 1f,
        infiniteRepeatable(tween(durationMs, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "y",
    )
    this.graphicsLayer { translationY = y * distance.dp.toPx() }
}
