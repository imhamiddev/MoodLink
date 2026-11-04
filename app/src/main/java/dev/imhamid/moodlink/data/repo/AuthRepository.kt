package dev.imhamid.moodlink.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.imhamid.moodlink.data.model.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) {
    val currentUid: String? get() = auth.currentUser?.uid

    val authState: Flow<String?> = callbackFlow {
        val l = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(l)
        awaitClose { auth.removeAuthStateListener(l) }
    }

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    suspend fun signUp(name: String, email: String, password: String) {
        val uid = auth.createUserWithEmailAndPassword(email.trim(), password).await().user!!.uid
        val profile = UserProfile(
            uid = uid,
            displayName = name.trim(),
            email = email.trim(),
            inviteCode = reserveInviteCode(uid),
        )
        db.collection("users").document(uid).set(profile).await()
    }

    /** Claims a unique code via a `inviteCodes/{code}` doc so lookups are O(1). */
    private suspend fun reserveInviteCode(uid: String): String {
        repeat(8) {
            val code = InviteCode.generate()
            val ref = db.collection("inviteCodes").document(code)
            val claimed = db.runTransaction { tx ->
                if (tx.get(ref).exists()) false else { tx.set(ref, mapOf("uid" to uid)); true }
            }.await()
            if (claimed) return code
        }
        error("Could not allocate an invite code")
    }

    fun signOut() = auth.signOut()
}
