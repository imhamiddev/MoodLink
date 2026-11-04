package dev.imhamid.moodlink.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.imhamid.moodlink.MainActivity
import dev.imhamid.moodlink.data.model.Friend
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

private val Ink = Color(0xFF2B1B5E)
private val InkSoft = Color(0xFF6B5F8F)
private val Surface = Color(0xFFF3ECFF)

class MoodWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val ep = WidgetEntryPoint.get(context)
        val uid = ep.auth().currentUid
        // One-shot read; the periodic worker + in-app updates keep it fresh.
        val friends = uid?.let { withTimeoutOrNull(8_000) { ep.moods().observeFriends(it).first() } }.orEmpty()
        provideContent { Content(signedIn = uid != null, friends = friends) }
    }

    @Composable
    private fun Content(signedIn: Boolean, friends: List<Friend>) {
        Box(
            GlanceModifier.fillMaxSize().cornerRadius(28.dp).background(Surface)
                .clickable(actionStartActivity<MainActivity>()).padding(12.dp),
        ) {
            when {
                !signedIn -> Empty("Open MoodLink to log in 💜")
                friends.isEmpty() -> Empty("Connect with someone to see their mood 💌")
                else -> LazyColumn {
                    items(friends.take(6)) { f -> Row(f) }
                }
            }
        }
    }

    @Composable
    private fun Row(f: Friend) {
        val mood = f.profile.mood
        Row(
            GlanceModifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                GlanceModifier.size(40.dp).cornerRadius(20.dp).background(mood?.tint ?: Color(0xFFD9CCFF)),
                contentAlignment = Alignment.Center,
            ) { Text(mood?.emoji ?: "💭", style = TextStyle(fontSize = 20.sp)) }
            Spacer(GlanceModifier.width(10.dp))
            Column {
                Text(
                    f.profile.displayName,
                    style = TextStyle(color = ColorProvider(Ink), fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    maxLines = 1,
                )
                Text(
                    mood?.let { "is ${it.label.lowercase()} now" } ?: "no mood yet",
                    style = TextStyle(color = ColorProvider(InkSoft), fontSize = 12.sp),
                    maxLines = 1,
                )
            }
        }
    }

    @Composable
    private fun Empty(text: String) {
        Box(GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text, style = TextStyle(color = ColorProvider(Ink), fontSize = 14.sp))
        }
    }
}
