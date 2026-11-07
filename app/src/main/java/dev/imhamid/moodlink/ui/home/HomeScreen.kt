package dev.imhamid.moodlink.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.imhamid.moodlink.ui.profile.ProfileScreen

private enum class Page { Mood, Friends, Profile }

/** The mood slider is home. Friends and Profile slide in over it. */
@Composable
fun HomeScreen(vm: HomeViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    var page by remember { mutableStateOf(Page.Mood) }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    BackHandler(enabled = page != Page.Mood) { page = Page.Mood }

    Box(Modifier.fillMaxSize()) {
        MoodScreen(
            name = s.me?.displayName.orEmpty(),
            currentMood = s.me?.mood,
            requestCount = s.requests.size,
            onShare = vm::shareMood,
            onFriends = { page = Page.Friends },
            onProfile = { page = Page.Profile },
        )

        AnimatedVisibility(
            visible = page == Page.Friends,
            enter = slideInHorizontally(tween(240)) { if (rtl) -it else it } + fadeIn(tween(240)),
            exit = slideOutHorizontally(tween(200)) { if (rtl) -it else it } + fadeOut(tween(200)),
        ) {
            FriendsScreen(s, onBack = { page = Page.Mood }, onAccept = vm::accept, onDecline = vm::decline)
        }

        AnimatedVisibility(
            visible = page == Page.Profile,
            enter = slideInHorizontally(tween(240)) { if (rtl) -it else it } + fadeIn(tween(240)),
            exit = slideOutHorizontally(tween(200)) { if (rtl) -it else it } + fadeOut(tween(200)),
        ) {
            ProfileScreen(state = s, onBack = { page = Page.Mood }, onLogout = vm::signOut)
        }
    }
}
