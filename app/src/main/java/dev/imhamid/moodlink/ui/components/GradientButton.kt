package dev.imhamid.moodlink.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.ui.theme.CtaGradient
import dev.imhamid.moodlink.ui.theme.Purple500

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val alpha by animateFloatAsState(if (enabled) 1f else .5f, label = "btnAlpha")
    Box(
        modifier
            .bounceClick(enabled = enabled && !loading, onClick = onClick)
            .fillMaxWidth()
            .height(58.dp)
            .alpha(alpha)
            .shadow(10.dp, CircleShape, ambientColor = Purple500, spotColor = Purple500)
            .clip(CircleShape)
            .background(CtaGradient),
        contentAlignment = Alignment.Center,
    ) {
        Crossfade(targetState = loading, label = "btnContent") { busy ->
            if (busy) CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.5.dp)
            else Crossfade(targetState = text, label = "btnText") {
                Text(it, color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
