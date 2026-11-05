package dev.imhamid.moodlink

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.imhamid.moodlink.data.repo.AuthRepository
import dev.imhamid.moodlink.i18n.AppLanguage
import dev.imhamid.moodlink.ui.auth.AuthScreen
import dev.imhamid.moodlink.ui.home.HomeScreen
import dev.imhamid.moodlink.ui.theme.MoodLinkTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var auth: AuthRepository

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLanguage.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodLinkTheme {
                val uid by auth.authState.collectAsState(initial = auth.currentUid)
                // Log in / log out cross-fades with a soft zoom. Home gets its own ViewModel per account.
                AnimatedContent(
                    targetState = uid,
                    transitionSpec = {
                        (fadeIn(tween(450)) + scaleIn(tween(450), initialScale = .94f)) togetherWith fadeOut(tween(250))
                    },
                    label = "root",
                ) { id ->
                    if (id == null) AuthScreen() else HomeScreen(vm = hiltViewModel(key = id))
                }
            }
        }
    }
}
