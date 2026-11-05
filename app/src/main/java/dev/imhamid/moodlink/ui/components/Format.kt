package dev.imhamid.moodlink.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.imhamid.moodlink.R
import java.text.NumberFormat
import java.util.Locale

/** "just now", "5 min ago", "3 h ago", "2 d ago" in the chosen language. Null when there is no time. */
@Composable
fun relativeTime(timestampMs: Long, nowMs: Long = System.currentTimeMillis()): String? {
    if (timestampMs <= 0L) return null
    val minutes = ((nowMs - timestampMs) / 60_000L).coerceAtLeast(0L).toInt()
    return when {
        minutes < 1 -> stringResource(R.string.time_now)
        minutes < 60 -> stringResource(R.string.time_minutes, minutes)
        minutes < 60 * 24 -> stringResource(R.string.time_hours, minutes / 60)
        else -> stringResource(R.string.time_days, minutes / (60 * 24))
    }
}

/** Digits in the script of the chosen language (Persian digits for Persian). */
fun formatNumber(n: Int): String = NumberFormat.getInstance(Locale.getDefault()).format(n)
