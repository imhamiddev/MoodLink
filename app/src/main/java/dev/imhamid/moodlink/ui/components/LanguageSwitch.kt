package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.R
import dev.imhamid.moodlink.i18n.AppLanguage
import dev.imhamid.moodlink.i18n.Lang
import dev.imhamid.moodlink.i18n.findActivity
import dev.imhamid.moodlink.ui.theme.Ink

/** Two-option switch between English and Persian. Each name is written in its own language. */
@Composable
fun LanguageSwitch(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val current = AppLanguage.current(context)
    Row(
        modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = .7f)).padding(4.dp),
    ) {
        Lang.entries.forEach { lang ->
            val selected = lang == current
            Box(
                Modifier
                    .defaultMinSize(minWidth = 88.dp, minHeight = 44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) Ink else Color.Transparent)
                    .clickable { context.findActivity()?.let { AppLanguage.switchTo(it, lang) } }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(if (lang == Lang.EN) R.string.language_en else R.string.language_fa),
                    color = if (selected) Color.White else Ink,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}
