package dev.imhamid.moodlink.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/** Friends, default 4x2. */
class MoodWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MoodWidget()
}

/** Friends spotlight, default 2x2. */
class MoodSpotlightReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MoodSpotlightWidget()
}

/** Friends list, default 4x4. */
class MoodListReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MoodListWidget()
}

/** My mood, default 2x1. */
class MyMoodReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyMoodWidget()
}
