package dev.imhamid.moodlink.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.imhamid.moodlink.widget.MoodWidgets
import dev.imhamid.moodlink.data.model.*
import dev.imhamid.moodlink.data.repo.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val me: UserProfile? = null,
    val friends: List<Friend> = emptyList(),
    val requests: List<IncomingRequest> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val moods: MoodRepository,
    private val connections: ConnectionRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val uid = auth.currentUid.orEmpty()

    val state: StateFlow<HomeState> = combine(
        moods.observeProfile(uid), moods.observeFriends(uid), moods.observeIncoming(uid),
    ) { me, friends, reqs -> HomeState(me, friends, reqs) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeState())

    init {
        // Keep the home-screen widget in sync while the app is open.
        viewModelScope.launch { moods.observeFriends(uid).collect { MoodWidgets.updateAll(context) } }
        viewModelScope.launch { moods.observeProfile(uid).collect { MoodWidgets.updateAll(context) } }
    }

    fun shareMood(mood: Mood) = viewModelScope.launch { moods.setMood(uid, mood) }
    fun accept(id: String) = viewModelScope.launch { connections.accept(id) }
    fun decline(id: String) = viewModelScope.launch { connections.remove(id) }
    fun signOut() = viewModelScope.launch { auth.signOut() }
}
