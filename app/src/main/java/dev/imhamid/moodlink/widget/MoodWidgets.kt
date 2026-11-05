package dev.imhamid.moodlink.widget

import android.content.Context
import androidx.glance.appwidget.updateAll

/** Refreshes every MoodLink widget, whatever size it was placed at. */
object MoodWidgets {
    suspend fun updateAll(context: Context) {
        MoodWidget().updateAll(context)
        MoodSpotlightWidget().updateAll(context)
        MoodListWidget().updateAll(context)
        MyMoodWidget().updateAll(context)
    }
}
