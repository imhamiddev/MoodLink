package dev.imhamid.moodlink.data.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import dev.imhamid.moodlink.R

/** Moods a user can share. [id] is what gets stored in the `mood_id` column; [labelRes] is the name shown to people and [tint] is its one flat colour. */
enum class Mood(val id: String, val emoji: String, @StringRes val labelRes: Int, val tint: Color) {
    HAPPY("happy", "😄", R.string.mood_happy, Color(0xFFF4C542)),
    LOVE("love", "🥰", R.string.mood_love, Color(0xFFF29AB4)),
    CALM("calm", "😌", R.string.mood_calm, Color(0xFF8FD1B8)),
    SLEEPY("sleepy", "😴", R.string.mood_sleepy, Color(0xFFB3A8E6)),
    TIRED("tired", "🥱", R.string.mood_tired, Color(0xFFF2B48A)),
    SAD("sad", "😢", R.string.mood_sad, Color(0xFF8FB0EA)),
    MISS_YOU("miss_you", "🥺", R.string.mood_miss_you, Color(0xFFD3A3E8)),
    ANGRY("angry", "😡", R.string.mood_angry, Color(0xFFE8765A)),
    ANXIOUS("anxious", "😰", R.string.mood_anxious, Color(0xFF9CCBEA)),
    EXCITED("excited", "🤩", R.string.mood_excited, Color(0xFFF7A93B));

    companion object {
        fun fromId(id: String?): Mood? = entries.firstOrNull { it.id == id }

        /** The moods ordered from worst to best, so a slider can walk through them. */
        val Spectrum: List<Mood> = listOf(ANGRY, SAD, MISS_YOU, ANXIOUS, TIRED, SLEEPY, CALM, HAPPY, EXCITED, LOVE)

        /** Slider position (0..1) of [mood] on the [Spectrum]. */
        fun positionOf(mood: Mood): Float = Spectrum.indexOf(mood).toFloat() / (Spectrum.size - 1)

        /** The mood nearest to a slider [position] (0..1). */
        fun atPosition(position: Float): Mood =
            Spectrum[(position.coerceIn(0f, 1f) * (Spectrum.size - 1)).let { Math.round(it) }]
    }
}
