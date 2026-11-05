package dev.imhamid.moodlink.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Row of `public.profiles`. */
@Serializable
data class UserProfile(
    val id: String = "",
    @SerialName("display_name") val displayName: String = "",
    val email: String = "",
    /** Short shareable code others type to send a connection request. */
    @SerialName("invite_code") val inviteCode: String = "",
    @SerialName("mood_id") val moodId: String? = null,
    @SerialName("mood_updated_at") val moodUpdatedAt: Long = 0L,
) {
    val uid: String get() = id
    val mood: Mood? get() = Mood.fromId(moodId)

    companion object {
        /** Columns the app reads; keeps `created_at` out of the payload. */
        const val COLUMNS = "id,display_name,email,invite_code,mood_id,mood_updated_at"
    }
}
