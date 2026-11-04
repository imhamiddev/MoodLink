package dev.imhamid.moodlink.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : ViewModel() {
    private val uid = auth.currentUid.orEmpty()

    val state: StateFlow<HomeState> = combine(
        moods.observeProfile(uid), moods.observeFriends(uid), moods.observeIncoming(uid),
    ) { me, friends, reqs -> HomeState(me, friends, reqs) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeState())

    fun shareMood(mood: Mood, note: String) = viewModelScope.launch { moods.setMood(uid, mood, note) }
    fun accept(id: String) = viewModelScope.launch { connections.accept(id) }
    fun decline(id: String) = viewModelScope.launch { connections.remove(id) }
    fun signOut() = auth.signOut()
}
