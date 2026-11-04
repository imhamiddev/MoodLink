package dev.imhamid.moodlink.data.repo

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val client: SupabaseClient) {

    val currentUid: String? get() = client.auth.currentUserOrNull()?.id

    /** Emits the signed-in user's id, or null when signed out. */
    val authState: Flow<String?> = client.auth.sessionStatus
        .filter { it !is SessionStatus.Initializing }
        .map { (it as? SessionStatus.Authenticated)?.session?.user?.id }
        .distinctUntilChanged()

    suspend fun signIn(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email.trim()
            this.password = password
        }
    }

    /**
     * The `profiles` row (including the invite code) is created by the
     * `handle_new_user` database trigger, so the client only sends the name.
     */
    suspend fun signUp(name: String, email: String, password: String) {
        client.auth.signUpWith(Email) {
            this.email = email.trim()
            this.password = password
            data = buildJsonObject { put("display_name", name.trim()) }
        }
        // With "Confirm email" off Supabase already returns a session; this is only a safety net.
        if (currentUid == null) signIn(email, password)
    }

    suspend fun signOut() = client.auth.signOut()
}
