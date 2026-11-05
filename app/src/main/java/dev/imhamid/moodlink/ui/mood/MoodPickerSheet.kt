package dev.imhamid.moodlink.ui.mood

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.R
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.components.MoodTile
import dev.imhamid.moodlink.ui.components.PrimaryButton
import dev.imhamid.moodlink.ui.theme.Accent
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.Paper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodPickerSheet(
    current: Mood?,
    onDismiss: () -> Unit,
    onShare: (Mood) -> Unit,
) {
    var selected by remember { mutableStateOf(current) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Paper) {
        Column(
            Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp).navigationBarsPadding().imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.picker_title), style = MaterialTheme.typography.headlineMedium, color = Ink)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.heightIn(max = 420.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(Mood.entries) { m -> MoodTile(m, selected == m, { selected = m }) }
            }
            PrimaryButton(
                stringResource(R.string.picker_share), enabled = selected != null,
                onClick = { selected?.let(onShare) },
                container = Accent, content = androidx.compose.ui.graphics.Color.White,
            )
        }
    }
}
