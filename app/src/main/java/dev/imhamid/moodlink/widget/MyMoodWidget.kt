package dev.imhamid.moodlink.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.imhamid.moodlink.MainActivity
import dev.imhamid.moodlink.data.model.UserProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/** Shows my own current mood; tapping opens the app to change it. Default 2x1, resizable. */
class MyMoodWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val ep = WidgetEntryPoint.get(context)
        val uid = ep.auth().currentUid
        val me = uid?.let { withTimeoutOrNull(8_000) { ep.moods().observeProfile(it).first() } }
        provideContent { Content(uid != null, me) }
    }

    @Composable
    private fun Content(signedIn: Boolean, me: UserProfile?) {
        val size = LocalSize.current
        val mood = me?.mood
        Box(
            GlanceModifier.fillMaxSize().cornerRadius(26.dp).background(mood?.tint ?: WSurface)
                .clickable(actionStartActivity<MainActivity>()).padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                !signedIn || me == null -> Text(
                    "Open MoodLink 💜",
                    style = TextStyle(color = ColorProvider(WInk), fontSize = 13.sp, textAlign = TextAlign.Center),
                )
                size.width < 150.dp || size.height >= 110.dp -> Column(
                    GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(mood?.emoji ?: "💭", style = TextStyle(fontSize = 34.sp))
                    Text(
                        mood?.label ?: "Set your mood",
                        style = TextStyle(color = ColorProvider(WInk), fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center),
                        maxLines = 1,
                    )
                }
                else -> Row(GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Text(mood?.emoji ?: "💭", style = TextStyle(fontSize = 30.sp))
                    Spacer(GlanceModifier.width(10.dp))
                    Column {
                        Text("I'm feeling", style = TextStyle(color = ColorProvider(WInkSoft), fontSize = 11.sp))
                        Text(
                            mood?.label ?: "Set your mood",
                            style = TextStyle(color = ColorProvider(WInk), fontWeight = FontWeight.Bold, fontSize = 15.sp),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
