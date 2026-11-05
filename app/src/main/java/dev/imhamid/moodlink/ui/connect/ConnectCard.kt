package dev.imhamid.moodlink.ui.connect

import dev.imhamid.moodlink.R
import androidx.compose.ui.res.stringResource
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
    val shareText = stringResource(R.string.connect_share_text, myCode)
    val chooserTitle = stringResource(R.string.connect_chooser)
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFDCC6FF), Color(0xFFF6D6EE))))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.connect_my_code), style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedContent(myCode.ifBlank { "······" }, modifier = Modifier.weight(1f), label = "code") { code ->
                Text(code, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 5.sp, color = Ink)
            }
            Box(
                Modifier.bounceClick {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        ctx.startActivity(Intent.createChooser(send, chooserTitle))
                    }
                    .clip(CircleShape).background(Color.White.copy(alpha = .85f))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) { Text(stringResource(R.string.connect_share), fontWeight = FontWeight.SemiBold, color = Ink) }
        }
        HorizontalDivider(color = Color.White.copy(alpha = .7f))
        Text(stringResource(R.string.connect_have_code), style = MaterialTheme.typography.titleMedium)
        MoodField(vm.code, vm::onCode, stringResource(R.string.connect_enter_code))
        val msg = vm.message
        AnimatedVisibility(msg != null, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            Text(
                msg?.let { stringResource(it) }.orEmpty(), style = MaterialTheme.typography.bodyMedium,
                color = if (vm.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
        }
        GradientButton(stringResource(R.string.connect_send), vm::send, enabled = vm.code.length == 6, loading = vm.loading)
    }
}
