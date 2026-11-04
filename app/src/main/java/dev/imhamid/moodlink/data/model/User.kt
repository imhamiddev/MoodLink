package dev.imhamid.moodlink.data.model

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    /** Short shareable code others type to send a connection request. */
    val inviteCode: String = "",
    val moodId: String? = null,
    val note: String = "",
    val moodUpdatedAt: Long = 0L,
) {
    val mood: Mood? get() = Mood.fromId(moodId)
}
