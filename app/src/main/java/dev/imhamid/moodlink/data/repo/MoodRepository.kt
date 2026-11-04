package dev.imhamid.moodlink.data.repo

import com.google.firebase.firestore.FirebaseFirestore
import dev.imhamid.moodlink.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoodRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val connections: ConnectionRepository,
) {
    suspend fun setMood(uid: String, mood: Mood, note: String) {
        db.collection("users").document(uid).update(
            mapOf("moodId" to mood.id, "note" to note.trim(), "moodUpdatedAt" to System.currentTimeMillis())
        ).await()
    }

    fun observeProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val reg = db.collection("users").document(uid).addSnapshotListener { s, _ ->
            trySend(s?.toObject(UserProfile::class.java))
        }
        awaitClose { reg.remove() }
    }

    /** Live list of accepted friends with their latest mood. */
    fun observeFriends(me: String): Flow<List<Friend>> =
        connections.observe(me).flatMapLatest { list ->
            val accepted = list.filter { it.connectionStatus == ConnectionStatus.ACCEPTED }
            if (accepted.isEmpty()) flowOf(emptyList())
            else combine(accepted.map { c ->
                observeProfile(c.otherUid(me)).map { p -> p?.let { Friend(it, c.id) } }
            }) { arr -> arr.filterNotNull().sortedByDescending { it.profile.moodUpdatedAt } }
        }

    fun observeIncoming(me: String): Flow<List<IncomingRequest>> =
        connections.observe(me).map { list ->
            list.filter { it.connectionStatus == ConnectionStatus.PENDING && it.requesterId != me }
                .mapNotNull { c -> connections.profile(c.requesterId)?.let { IncomingRequest(it, c.id) } }
        }
}
