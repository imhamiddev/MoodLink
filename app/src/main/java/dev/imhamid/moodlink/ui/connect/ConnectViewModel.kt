package dev.imhamid.moodlink.ui.connect

import dev.imhamid.moodlink.R
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
    /** String resource id of the status line, so it follows the chosen language. */
    var message by mutableStateOf<Int?>(null); private set
    var isError by mutableStateOf(false); private set

    fun onCode(v: String) { code = InviteCode.normalize(v).take(InviteCode.LENGTH); message = null }

    fun send() {
        val me = auth.currentUid ?: return
        loading = true
        viewModelScope.launch {
            runCatching { repo.sendRequest(me, code) }
                .onSuccess { message = R.string.connect_sent; isError = false; code = "" }
                .onFailure {
                    message = (it as? ConnectError)?.messageRes ?: R.string.connect_err_generic
                    isError = true
                }
            loading = false
        }
    }
}
