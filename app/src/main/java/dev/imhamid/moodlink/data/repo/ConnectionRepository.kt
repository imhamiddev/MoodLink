package dev.imhamid.moodlink.data.repo

import com.google.firebase.firestore.FirebaseFirestore
import dev.imhamid.moodlink.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

class ConnectError(message: String) : Exception(message)

@Singleton
class ConnectionRepository @Inject constructor(private val db: FirebaseFirestore) {

    private val connections get() = db.collection("connections")
    private val users get() = db.collection("users")

    /** Sends a request to whoever owns [rawCode]. */
    suspend fun sendRequest(me: String, rawCode: String) {
        val code = InviteCode.normalize(rawCode)
        if (code.length != InviteCode.LENGTH) throw ConnectError("That code doesn't look right")
        val target = db.collection("inviteCodes").document(code).get().await().getString("uid")
            ?: throw ConnectError("No one has this code")
        if (target == me) throw ConnectError("That's your own code 🙂")

        val id = listOf(me, target).sorted().joinToString("_")
        val ref = connections.document(id)
        if (ref.get().await().exists()) throw ConnectError("You're already connected or waiting for a reply")
        ref.set(
            Connection(id, listOf(me, target), requesterId = me, createdAt = System.currentTimeMillis())
        ).await()
    }

    suspend fun accept(connectionId: String) {
        connections.document(connectionId).update("status", ConnectionStatus.ACCEPTED.name).await()
    }

    suspend fun remove(connectionId: String) {
        connections.document(connectionId).delete().await()
    }

    fun observe(me: String): Flow<List<Connection>> = callbackFlow {
        val reg = connections.whereArrayContains("members", me).addSnapshotListener { snap, _ ->
            trySend(snap?.toObjects(Connection::class.java).orEmpty())
        }
        awaitClose { reg.remove() }
    }

    suspend fun profile(uid: String): UserProfile? =
        users.document(uid).get().await().toObject(UserProfile::class.java)
}
