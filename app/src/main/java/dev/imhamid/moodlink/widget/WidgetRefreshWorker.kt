package dev.imhamid.moodlink.widget

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class WidgetRefreshWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        MoodWidgets.updateAll(applicationContext)
        return Result.success()
    }

    companion object {
        private const val NAME = "mood_widget_refresh"

        fun schedule(context: Context) {
            val req = PeriodicWorkRequestBuilder<WidgetRefreshWorker>(15, TimeUnit.MINUTES)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(NAME, ExistingPeriodicWorkPolicy.KEEP, req)
        }
    }
}
