package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.ui.theme.GlassWhite

/** Frosted white surface used for cards, matching the references. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    container: Color = GlassWhite,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(1.dp, Color.White.copy(alpha = .85f)),
    ) { Column(Modifier.padding(18.dp), content = content) }
}
