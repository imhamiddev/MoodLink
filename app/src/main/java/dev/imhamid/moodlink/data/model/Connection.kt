package dev.imhamid.moodlink.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ConnectionStatus { PENDING, ACCEPTED }

/**
 * Row of `public.connections`. The id is the two uids sorted and joined with "_",
 * so a pair can only ever have one connection.
 */
@Serializable
data class Connection(
    val id: String = "",
    @SerialName("user_a") val userA: String = "",
    @SerialName("user_b") val userB: String = "",
    @SerialName("requester_id") val requesterId: String = "",
    val status: String = ConnectionStatus.PENDING.name,
    @SerialName("created_at") val createdAt: Long = 0L,
) {
    val members: List<String> get() = listOf(userA, userB)
    val connectionStatus: ConnectionStatus get() = ConnectionStatus.valueOf(status)
    fun otherUid(me: String) = if (userA == me) userB else userA
}
