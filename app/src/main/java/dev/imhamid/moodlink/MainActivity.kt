package dev.imhamid.moodlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import dagger.hilt.android.AndroidEntryPoint
import dev.imhamid.moodlink.data.repo.AuthRepository
import dev.imhamid.moodlink.ui.auth.AuthScreen
import dev.imhamid.moodlink.ui.home.HomeScreen
import dev.imhamid.moodlink.ui.theme.MoodLinkTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var auth: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodLinkTheme {
                val uid by auth.authState.collectAsState(initial = auth.currentUid)
                if (uid == null) AuthScreen() else HomeScreen()
            }
        }
    }
}
