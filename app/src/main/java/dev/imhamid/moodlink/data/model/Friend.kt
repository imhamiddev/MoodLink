package dev.imhamid.moodlink.data.model

/** A connected person plus their latest shared mood, as shown in the UI and widget. */
data class Friend(val profile: UserProfile, val connectionId: String)

/** A pending request addressed to the current user. */
data class IncomingRequest(val from: UserProfile, val connectionId: String)
