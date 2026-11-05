package dev.imhamid.moodlink.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.theme.Ink

/** Big pastel tile with the mood's character, like the "How are you feeling" grid. */
@Composable
fun MoodTile(mood: Mood, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bounce = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    val scale by animateFloatAsState(if (selected) 1.04f else 1f, bounce, label = "tile")
    val faceScale by animateFloatAsState(if (selected) 1.3f else 1f, bounce, label = "faceScale")
    val faceRot by animateFloatAsState(if (selected) 0f else -10f, bounce, label = "faceRot")
    val border by animateColorAsState(if (selected) mood.tintDark else Color.Transparent, label = "border")
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier
            .bounceClick(scaleDown = .95f, onClick = onClick)
            .height(132.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(Brush.verticalGradient(listOf(mood.tint, mood.tint.copy(alpha = .55f))))
            .border(3.dp, border, shape),
    ) {
        Text(
            mood.label, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
        )
        MoodFace(
            mood,
            Modifier.align(Alignment.BottomStart).padding(start = 14.dp, bottom = 10.dp).size(58.dp)
                .graphicsLayer { scaleX = faceScale; scaleY = faceScale; rotationZ = faceRot },
            squircle = true, animated = selected,
        )
        Box(
            Modifier.align(Alignment.BottomEnd).padding(12.dp).size(34.dp)
                .clip(CircleShape).background(Color.White.copy(alpha = .55f)),
            contentAlignment = Alignment.Center,
        ) { Crossfade(selected, label = "chip") { sel -> Text(if (sel) "✓" else "↗", color = Ink, fontSize = 16.sp) } }
    }
}
