package dev.imhamid.moodlink.data.repo

import dev.imhamid.moodlink.data.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoodRepository @Inject constructor(
    private val client: SupabaseClient,
    private val connections: ConnectionRepository,
    private val watcher: TableWatcher,
) {
    suspend fun setMood(uid: String, mood: Mood) {
        client.from("profiles").update({
            set("mood_id", mood.id)
            set("mood_updated_at", System.currentTimeMillis())
        }) { filter { eq("id", uid) } }
    }

    fun observeProfile(uid: String): Flow<UserProfile?> =
        watcher.watch("profiles") { fetchProfile(uid) }

    private suspend fun fetchProfile(uid: String): UserProfile? =
        client.from("profiles").select(Columns.raw(UserProfile.COLUMNS)) {
            filter { eq("id", uid) }
        }.decodeSingleOrNull()

    /** Live list of accepted friends with their latest mood. */
    fun observeFriends(me: String): Flow<List<Friend>> =
        watcher.watch("connections", "profiles") {
            val accepted = connections.fetch().filter { it.connectionStatus == ConnectionStatus.ACCEPTED }
            val byId = connections.profiles(accepted.map { it.otherUid(me) }).associateBy { it.id }
            accepted.mapNotNull { c -> byId[c.otherUid(me)]?.let { Friend(it, c.id) } }
                .sortedByDescending { it.profile.moodUpdatedAt }
        }

    fun observeIncoming(me: String): Flow<List<IncomingRequest>> =
        watcher.watch("connections") {
            val pending = connections.fetch()
                .filter { it.connectionStatus == ConnectionStatus.PENDING && it.requesterId != me }
            val byId = connections.profiles(pending.map { it.requesterId }).associateBy { it.id }
            pending.mapNotNull { c -> byId[c.requesterId]?.let { IncomingRequest(it, c.id) } }
        }
}
