package dev.imhamid.moodlink

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.imhamid.moodlink.widget.WidgetRefreshWorker

@HiltAndroidApp
class MoodLinkApp : Application() {
    override fun onCreate() {
        super.onCreate()
        WidgetRefreshWorker.schedule(this)
    }
}
