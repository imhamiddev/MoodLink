package dev.imhamid.moodlink.widget

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.imhamid.moodlink.data.repo.AuthRepository
import dev.imhamid.moodlink.data.repo.MoodRepository

/** Lets the widget (which Hilt can't inject directly) reach the repositories. */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun auth(): AuthRepository
    fun moods(): MoodRepository

    companion object {
        fun get(context: Context): WidgetEntryPoint =
            EntryPointAccessors.fromApplication(context.applicationContext, WidgetEntryPoint::class.java)
    }
}
