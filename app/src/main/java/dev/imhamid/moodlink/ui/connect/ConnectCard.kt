package dev.imhamid.moodlink.ui.connect

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.imhamid.moodlink.ui.components.*

/** Shows my code (shareable) and a field to enter someone else's. */
@Composable
fun ConnectCard(myCode: String, vm: ConnectViewModel = hiltViewModel()) {
    val ctx = LocalContext.current
    GlassCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Your code", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(myCode.ifBlank { "······" }, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Join me on MoodLink! My code is $myCode")
                    }
                    ctx.startActivity(Intent.createChooser(send, "Share code"))
                }) { Text("Share") }
            }
            HorizontalDivider()
            Text("Have a friend's code?", style = MaterialTheme.typography.titleMedium)
            MoodField(vm.code, vm::onCode, "Enter 6-character code")
            vm.message?.let {
                Text(it, color = if (vm.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium)
            }
            GradientButton("Send request", vm::send, enabled = vm.code.length == 6, loading = vm.loading)
        }
    }
}
