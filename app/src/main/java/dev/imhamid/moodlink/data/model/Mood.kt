package dev.imhamid.moodlink.data.model

import androidx.compose.ui.graphics.Color

/** Moods a user can share. [id] is what gets stored in Firestore. */
enum class Mood(val id: String, val emoji: String, val label: String, val tint: Color, val tintDark: Color) {
    HAPPY("happy", "😄", "Happy", Color(0xFFFFE08A), Color(0xFFFFB800)),
    LOVE("love", "🥰", "In love", Color(0xFFFFC2D9), Color(0xFFFF5C93)),
    CALM("calm", "😌", "Calm", Color(0xFFBDEFE4), Color(0xFF3CC3A6)),
    SLEEPY("sleepy", "😴", "Sleepy", Color(0xFFD7D2FF), Color(0xFF8E83F0)),
    TIRED("tired", "🥱", "Tired", Color(0xFFFFD9C2), Color(0xFFFF8F5C)),
    SAD("sad", "😢", "Sad", Color(0xFFC3D3FF), Color(0xFF5B83F5)),
    MISS_YOU("miss_you", "🥺", "Miss you", Color(0xFFE6C8FF), Color(0xFFB66CF2)),
    ANGRY("angry", "😡", "Angry", Color(0xFFFFC7B0), Color(0xFFFF6A3D)),
    ANXIOUS("anxious", "😰", "Anxious", Color(0xFFCFE8FF), Color(0xFF4FA3E8)),
    EXCITED("excited", "🤩", "Excited", Color(0xFFFFE3A3), Color(0xFFFF9E1F));

    companion object {
        fun fromId(id: String?): Mood? = entries.firstOrNull { it.id == id }
    }
}
