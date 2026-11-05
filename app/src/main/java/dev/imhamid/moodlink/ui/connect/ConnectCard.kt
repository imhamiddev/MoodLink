package dev.imhamid.moodlink.ui.connect

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.imhamid.moodlink.ui.components.*
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft

/** Shows my code (shareable) and a field to enter someone else's. */
@Composable
fun ConnectCard(myCode: String, vm: ConnectViewModel = hiltViewModel()) {
    val ctx = LocalContext.current
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFDCC6FF), Color(0xFFF6D6EE))))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Your invite code", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                myCode.ifBlank { "······" }, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold,
                letterSpacing = 5.sp, color = Ink, modifier = Modifier.weight(1f),
            )
            Box(
                Modifier.clip(CircleShape).background(Color.White.copy(alpha = .85f))
                    .clickable {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Join me on MoodLink! My code is $myCode")
                        }
                        ctx.startActivity(Intent.createChooser(send, "Share code"))
                    }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) { Text("Share", fontWeight = FontWeight.SemiBold, color = Ink) }
        }
        HorizontalDivider(color = Color.White.copy(alpha = .7f))
        Text("Have a friend's code?", style = MaterialTheme.typography.titleMedium)
        MoodField(vm.code, vm::onCode, "Enter 6-character code")
        vm.message?.let {
            Text(
                it, style = MaterialTheme.typography.bodyMedium,
                color = if (vm.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
        }
        GradientButton("Send request", vm::send, enabled = vm.code.length == 6, loading = vm.loading)
    }
}
