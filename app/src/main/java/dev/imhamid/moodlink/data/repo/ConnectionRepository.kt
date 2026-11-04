package dev.imhamid.moodlink.data.repo

import dev.imhamid.moodlink.data.model.Connection
import dev.imhamid.moodlink.data.model.ConnectionStatus
import dev.imhamid.moodlink.data.model.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

class ConnectError(message: String) : Exception(message)

@Serializable private data class CodeParams(val code: String)
@Serializable private data class IdRow(val id: String)

@Singleton
class ConnectionRepository @Inject constructor(
    private val client: SupabaseClient,
    private val watcher: TableWatcher,
) {
    /** Sends a request to whoever owns [rawCode]. */
    suspend fun sendRequest(me: String, rawCode: String) {
        val code = InviteCode.normalize(rawCode)
        if (code.length != InviteCode.LENGTH) throw ConnectError("That code doesn't look right")

        val target = client.postgrest.rpc("find_user_by_code", CodeParams(code))
            .decodeList<IdRow>().firstOrNull()?.id
            ?: throw ConnectError("No one has this code")
        if (target == me) throw ConnectError("That's your own code 🙂")

        val (a, b) = listOf(me, target).sorted()
        val id = "${a}_$b"
        val existing = client.from("connections").select { filter { eq("id", id) } }
            .decodeSingleOrNull<Connection>()
        if (existing != null) throw ConnectError("You're already connected or waiting for a reply")

        client.from("connections").insert(
            Connection(
                id = id,
                userA = a,
                userB = b,
                requesterId = me,
                status = ConnectionStatus.PENDING.name,
                createdAt = System.currentTimeMillis(),
            )
        )
    }

    suspend fun accept(connectionId: String) {
        client.from("connections").update({ set("status", ConnectionStatus.ACCEPTED.name) }) {
            filter { eq("id", connectionId) }
        }
    }

    suspend fun remove(connectionId: String) {
        client.from("connections").delete { filter { eq("id", connectionId) } }
    }

    /** Connections I belong to; row level security already scopes the query to me. */
    suspend fun fetch(): List<Connection> = client.from("connections").select().decodeList()

    fun observe(): Flow<List<Connection>> = watcher.watch("connections") { fetch() }

    suspend fun profiles(ids: Collection<String>): List<UserProfile> =
        if (ids.isEmpty()) emptyList()
        else client.from("profiles").select(Columns.raw(UserProfile.COLUMNS)) {
            filter { isIn("id", ids.toList()) }
        }.decodeList()

    suspend fun profile(uid: String): UserProfile? = profiles(listOf(uid)).firstOrNull()
}
