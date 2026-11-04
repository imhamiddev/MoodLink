package dev.imhamid.moodlink.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import dev.imhamid.moodlink.ui.components.*

@Composable
fun AuthScreen(vm: AuthViewModel = hiltViewModel()) {
    Column(
        Modifier.fillMaxSize().systemBarsPadding().imePadding()
            .verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
    ) {
        Text("💜", fontSize = 64.sp)
        Text("MoodLink", style = MaterialTheme.typography.displayMedium)
        Text(
            "Share how you feel with the people who matter, right on their home screen.",
            style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (vm.signUpMode) MoodField(vm.name, vm::onName, "Your name")
                MoodField(vm.email, vm::onEmail, "Email", keyboardType = KeyboardType.Email)
                MoodField(vm.password, vm::onPassword, "Password (6+ characters)", password = true)
                vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }
                GradientButton(
                    if (vm.signUpMode) "Create account" else "Log in",
                    onClick = vm::submit, enabled = vm.canSubmit, loading = vm.loading,
                )
            }
        }
        TextButton(onClick = vm::toggleMode) {
            Text(if (vm.signUpMode) "Already have an account? Log in" else "New here? Create an account")
        }
    }
}
