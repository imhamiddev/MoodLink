package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.ui.theme.CtaGradient

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .alpha(if (enabled) 1f else .5f)
            .clip(CircleShape)
            .background(CtaGradient)
            .clickable(enabled = enabled && !loading, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.5.dp)
        else Text(text, color = Color.White, style = MaterialTheme.typography.labelLarge)
    }
}
