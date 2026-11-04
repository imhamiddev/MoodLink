package dev.imhamid.moodlink.data.model

enum class ConnectionStatus { PENDING, ACCEPTED }

/**
 * A link between two users. Document id is the two uids sorted and joined,
 * so a pair can only ever have one connection.
 */
data class Connection(
    val id: String = "",
    val members: List<String> = emptyList(),
    val requesterId: String = "",
    val status: String = ConnectionStatus.PENDING.name,
    val createdAt: Long = 0L,
) {
    val connectionStatus get() = ConnectionStatus.valueOf(status)
    fun otherUid(me: String) = members.first { it != me }
}
