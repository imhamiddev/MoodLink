package dev.imhamid.moodlink.ui.connect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.imhamid.moodlink.data.repo.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val repo: ConnectionRepository,
) : ViewModel() {
    var code by mutableStateOf(""); private set
    var loading by mutableStateOf(false); private set
    var message by mutableStateOf<String?>(null); private set
    var isError by mutableStateOf(false); private set

    fun onCode(v: String) { code = InviteCode.normalize(v).take(InviteCode.LENGTH); message = null }

    fun send() {
        val me = auth.currentUid ?: return
        loading = true
        viewModelScope.launch {
            runCatching { repo.sendRequest(me, code) }
                .onSuccess { message = "Request sent! They'll see it on their home screen."; isError = false; code = "" }
                .onFailure { message = it.message ?: "Couldn't send the request"; isError = true }
            loading = false
        }
    }
}
