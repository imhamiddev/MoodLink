package dev.imhamid.moodlink.ui.mood

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.components.GradientButton
import dev.imhamid.moodlink.ui.components.MoodTile
import dev.imhamid.moodlink.ui.components.entrance
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodPickerSheet(
    current: Mood?,
    onDismiss: () -> Unit,
    onShare: (Mood) -> Unit,
) {
    var selected by remember { mutableStateOf(current) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFFF6EEFF)) {
        Column(
            Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp).navigationBarsPadding().imePadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Light, color = InkSoft)) { append("How do you describe\n") }
                    withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = Ink)) { append("your feeling ") }
                    withStyle(SpanStyle(fontWeight = FontWeight.Light, color = InkSoft)) { append("today?") }
                },
                fontSize = 28.sp, lineHeight = 34.sp, modifier = Modifier.entrance(0),
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.heightIn(max = 340.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(Mood.entries) { i, m ->
                    MoodTile(m, selected == m, { selected = m }, Modifier.entrance(i + 1))
                }
            }

            GradientButton(
                "Share with my people", enabled = selected != null,
                onClick = { selected?.let(onShare) },
            )
        }
    }
}
