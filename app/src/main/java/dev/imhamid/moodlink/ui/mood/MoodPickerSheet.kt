package dev.imhamid.moodlink.ui.mood

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.components.GradientButton
import dev.imhamid.moodlink.ui.components.MoodField
import dev.imhamid.moodlink.ui.components.MoodTile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodPickerSheet(
    current: Mood?,
    onDismiss: () -> Unit,
    onShare: (Mood, String) -> Unit,
) {
    var selected by remember { mutableStateOf(current) }
    var note by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.background) {
        Column(Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp).navigationBarsPadding().imePadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("How are you feeling today?", style = MaterialTheme.typography.headlineMedium)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.heightIn(max = 380.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) { items(Mood.entries) { m -> MoodTile(m, selected == m, { selected = m }) } }
            MoodField(note, { note = it.take(60) }, "Add a short note (optional)")
            GradientButton("Share with my people", enabled = selected != null,
                onClick = { selected?.let { onShare(it, note) } })
        }
    }
}
