package dev.imhamid.moodlink.data.repo

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Turns "something changed in these tables" into a cold Flow: emits [load] once
 * on collect and again after every Realtime change. Row level security decides
 * which events this client receives, so a plain re-query stays correct and simple.
 */
@Singleton
class TableWatcher @Inject constructor(private val client: SupabaseClient) {

    fun <T> watch(vararg tables: String, load: suspend () -> T): Flow<T> = channelFlow {
        val channel = client.realtime.channel("watch-${tables.joinToString("-")}-${UUID.randomUUID()}")
        val changes = tables.map { name ->
            channel.postgresChangeFlow<PostgresAction>(schema = "public") { table = name }
        }

        runCatching { load() }.onSuccess { send(it) }
        changes.forEach { flow ->
            flow.onEach { runCatching { load() }.onSuccess { value -> send(value) } }.launchIn(this)
        }
        // If Realtime is unavailable the feed still shows the initial snapshot.
        runCatching { channel.subscribe(blockUntilSubscribed = true) }

        try {
            awaitCancellation()
        } finally {
            withContext(NonCancellable) {
                runCatching { channel.unsubscribe() }
                runCatching { client.realtime.removeChannel(channel) }
            }
        }
    }
}
