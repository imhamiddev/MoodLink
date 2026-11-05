package dev.imhamid.moodlink.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

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
