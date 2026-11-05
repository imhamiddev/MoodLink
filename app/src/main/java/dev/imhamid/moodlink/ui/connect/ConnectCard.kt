package dev.imhamid.moodlink.ui.connect

import dev.imhamid.moodlink.ui.components.Panel
import dev.imhamid.moodlink.ui.components.PrimaryButton
import dev.imhamid.moodlink.ui.components.SecondaryButton
import dev.imhamid.moodlink.ui.components.MoodField
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
dev.imhamid.moodlink.R
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
import dev.imhamid.moodlink.ui.theme.Danger
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.Line
import androidx.compose.ui.text.font.FontFamily
import dev.imhamid.moodlink.ui.theme.InkSoft

/** Shows my code (shareable) and a field to enter someone else's. */
@Composable
fun ConnectCard(myCode: String, vm: ConnectViewModel = hiltViewModel()) {
    val ctx = LocalContext.current
    val shareText = stringResource(R.string.connect_share_text, myCode)
    val chooserTitle = stringResource(R.string.connect_chooser)
    Panel(Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.connect_my_code), style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                myCode.ifBlank { "······" }, modifier = Modifier.weight(1f),
                fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp, color = Ink,
            )
            SecondaryButton(stringResource(R.string.connect_share), fillWidth = false, onClick = {
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                ctx.startActivity(Intent.createChooser(send, chooserTitle))
            })
        }
        HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 14.dp))
        Text(stringResource(R.string.connect_have_code), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        MoodField(vm.code, vm::onCode, stringResource(R.string.connect_enter_code))
        Spacer(Modifier.height(10.dp))
        val msg = vm.message
        AnimatedVisibility(msg != null, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            Text(
                msg?.let { stringResource(it) }.orEmpty(), style = MaterialTheme.typography.bodyMedium,
                color = if (vm.isError) Danger else Ink,
            )
        }
        Spacer(Modifier.height(12.dp))
        PrimaryButton(stringResource(R.string.connect_send), vm::send, enabled = vm.code.length == 6, loading = vm.loading)
    }
}
