package dev.imhamid.moodlink.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.imhamid.moodlink.data.repo.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {
    var name by mutableStateOf(""); private set
    var email by mutableStateOf(""); private set
    var password by mutableStateOf(""); private set
    var signUpMode by mutableStateOf(true); private set
    /** False = welcome page, true = the sign up / log in form. */
    var showForm by mutableStateOf(false); private set
    var loading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    fun onName(v: String) { name = v }
    fun onEmail(v: String) { email = v }
    fun onPassword(v: String) { password = v }
    fun start(signUp: Boolean) { signUpMode = signUp; error = null; showForm = true }
    fun back() { showForm = false; error = null }
    fun toggleMode() { signUpMode = !signUpMode; error = null }

    val canSubmit get() = email.contains("@") && password.length >= 6 && (!signUpMode || name.isNotBlank())

    fun submit() {
        if (!canSubmit || loading) return
        loading = true; error = null
        viewModelScope.launch {
            runCatching {
                if (signUpMode) repo.signUp(name, email, password) else repo.signIn(email, password)
            }.onFailure { error = it.localizedMessage ?: "Something went wrong" }
            loading = false
        }
    }
}
