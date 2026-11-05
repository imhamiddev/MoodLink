package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.ui.theme.Line
import dev.imhamid.moodlink.ui.theme.PanelShape
import dev.imhamid.moodlink.ui.theme.Raised

/** A raised sheet of paper with a 1dp line. Depth comes from the line, not a shadow. */
@Composable
fun Panel(
    modifier: Modifier = Modifier,
    container: Color = Raised,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier.clip(PanelShape).background(container).border(1.dp, Line, PanelShape).padding(16.dp),
        content = content,
    )
}
