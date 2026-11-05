package dev.imhamid.moodlink.data.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import dev.imhamid.moodlink.R

/** Moods a user can share. [id] is what gets stored in the `mood_id` column; [labelRes] is the name shown to people. */
enum class Mood(val id: String, val emoji: String, @StringRes val labelRes: Int, val tint: Color, val tintDark: Color) {
    HAPPY("happy", "😄", R.string.mood_happy, Color(0xFFFFE08A), Color(0xFFFFB800)),
    LOVE("love", "🥰", R.string.mood_love, Color(0xFFFFC2D9), Color(0xFFFF5C93)),
    CALM("calm", "😌", R.string.mood_calm, Color(0xFFBDEFE4), Color(0xFF3CC3A6)),
    SLEEPY("sleepy", "😴", R.string.mood_sleepy, Color(0xFFD7D2FF), Color(0xFF8E83F0)),
    TIRED("tired", "🥱", R.string.mood_tired, Color(0xFFFFD9C2), Color(0xFFFF8F5C)),
    SAD("sad", "😢", R.string.mood_sad, Color(0xFFC3D3FF), Color(0xFF5B83F5)),
    MISS_YOU("miss_you", "🥺", R.string.mood_miss_you, Color(0xFFE6C8FF), Color(0xFFB66CF2)),
    ANGRY("angry", "😡", R.string.mood_angry, Color(0xFFFFC7B0), Color(0xFFFF6A3D)),
    ANXIOUS("anxious", "😰", R.string.mood_anxious, Color(0xFFCFE8FF), Color(0xFF4FA3E8)),
    EXCITED("excited", "🤩", R.string.mood_excited, Color(0xFFFFE3A3), Color(0xFFFF9E1F));

    companion object {
        fun fromId(id: String?): Mood? = entries.firstOrNull { it.id == id }
    }
}
