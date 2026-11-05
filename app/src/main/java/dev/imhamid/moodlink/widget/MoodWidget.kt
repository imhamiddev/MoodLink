package dev.imhamid.moodlink.widget

import dev.imhamid.moodlink.R
import dev.imhamid.moodlink.i18n.AppLanguage
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
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.imhamid.moodlink.MainActivity
import dev.imhamid.moodlink.data.model.Friend
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

internal val WInk = Color(0xFF1E1B16)
internal val WInkSoft = Color(0xFF5E574B)
internal val WSurface = Color(0xFFF5F0E6)
internal val WNeutral = Color(0xFFD9D1C0)

/**
 * Friends widget. One implementation, three layouts picked from the size the user gives it:
 *  - small (about 2x2): spotlight on the friend who changed mood most recently
 *  - wide (about 4x2): side-by-side tiles
 *  - tall (about 4x4): scrolling list
 * The subclasses below only exist so each size gets its own entry in the widget picker.
 */
open class MoodWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val ep = WidgetEntryPoint.get(context)
        val uid = ep.auth().currentUid
        // One-shot read; the periodic worker + in-app updates keep it fresh.
        val friends = uid?.let { withTimeoutOrNull(8_000) { ep.moods().observeFriends(it).first() } }.orEmpty()
        val strings = AppLanguage.wrap(context)
        provideContent { Content(strings, signedIn = uid != null, friends = friends) }
    }

    @Composable
    private fun Content(ctx: Context, signedIn: Boolean, friends: List<Friend>) {
        val size = LocalSize.current
        Box(
            GlanceModifier.fillMaxSize().cornerRadius(28.dp).background(WSurface)
                .clickable(actionStartActivity<MainActivity>()).padding(10.dp),
        ) {
            when {
                !signedIn -> Empty(ctx.getString(R.string.widget_open_login))
                friends.isEmpty() -> Empty(ctx.getString(R.string.widget_connect_hint))
                size.height >= 200.dp -> ListLayout(ctx, friends)
                size.width >= 180.dp -> RowLayout(ctx, friends, size.width.value)
                else -> Spotlight(ctx, friends.first())
            }
        }
    }

    @Composable
    private fun Spotlight(ctx: Context, f: Friend) {
        val mood = f.profile.mood
        Column(
            GlanceModifier.fillMaxSize().cornerRadius(22.dp).background(mood?.tint ?: WNeutral),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(mood?.emoji ?: "💭", style = TextStyle(fontSize = 38.sp))
            Spacer(GlanceModifier.height(4.dp))
            Text(
                f.profile.displayName,
                style = TextStyle(color = ColorProvider(WInk), fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center),
                maxLines = 1,
            )
            Text(
                ctx.getString(mood?.labelRes ?: R.string.widget_no_mood),
                style = TextStyle(color = ColorProvider(WInkSoft), fontSize = 12.sp, textAlign = TextAlign.Center),
                maxLines = 1,
            )
        }
    }

    @Composable
    private fun RowLayout(ctx: Context, friends: List<Friend>, widthDp: Float) {
        val count = (widthDp / 96f).toInt().coerceIn(2, 4)
        Row(GlanceModifier.fillMaxSize()) {
            friends.take(count).forEachIndexed { i, f ->
                if (i > 0) Spacer(GlanceModifier.width(8.dp))
                val mood = f.profile.mood
                Column(
                    GlanceModifier.defaultWeight().fillMaxHeight().cornerRadius(20.dp).background(mood?.tint ?: WNeutral),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(mood?.emoji ?: "💭", style = TextStyle(fontSize = 30.sp))
                    Spacer(GlanceModifier.height(2.dp))
                    Text(
                        f.profile.displayName,
                        style = TextStyle(color = ColorProvider(WInk), fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center),
                        maxLines = 1,
                    )
                    Text(
                        ctx.getString(mood?.labelRes ?: R.string.widget_no_mood),
                        style = TextStyle(color = ColorProvider(WInkSoft), fontSize = 11.sp, textAlign = TextAlign.Center),
                        maxLines = 1,
                    )
                }
            }
        }
    }

    @Composable
    private fun ListLayout(ctx: Context, friends: List<Friend>) {
        Column(GlanceModifier.fillMaxSize()) {
            Row(GlanceModifier.fillMaxWidth().padding(bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    ctx.getString(R.string.home_people),
                    style = TextStyle(color = ColorProvider(WInk), fontWeight = FontWeight.Bold, fontSize = 16.sp),
                    modifier = GlanceModifier.defaultWeight(),
                )
                Text("${friends.size}", style = TextStyle(color = ColorProvider(WInkSoft), fontSize = 13.sp))
            }
            LazyColumn {
                items(friends.take(8)) { f -> FriendRow(ctx, f) }
            }
        }
    }

    @Composable
    private fun FriendRow(ctx: Context, f: Friend) {
        val mood = f.profile.mood
        Row(
            GlanceModifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                GlanceModifier.size(40.dp).cornerRadius(20.dp).background(mood?.tint ?: WNeutral),
                contentAlignment = Alignment.Center,
            ) { Text(mood?.emoji ?: "💭", style = TextStyle(fontSize = 20.sp)) }
            Spacer(GlanceModifier.width(10.dp))
            Column {
                Text(
                    f.profile.displayName,
                    style = TextStyle(color = ColorProvider(WInk), fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    maxLines = 1,
                )
                Text(
                    ctx.getString(mood?.labelRes ?: R.string.widget_no_mood),
                    style = TextStyle(color = ColorProvider(WInkSoft), fontSize = 12.sp),
                    maxLines = 1,
                )
            }
        }
    }

    @Composable
    private fun Empty(text: String) {
        Box(GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text, style = TextStyle(color = ColorProvider(WInk), fontSize = 14.sp, textAlign = TextAlign.Center))
        }
    }
}

/** Default size 2x2 in the picker. */
class MoodSpotlightWidget : MoodWidget()

/** Default size 4x4 in the picker. */
class MoodListWidget : MoodWidget()
