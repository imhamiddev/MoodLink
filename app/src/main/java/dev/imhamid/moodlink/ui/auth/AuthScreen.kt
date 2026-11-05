package dev.imhamid.moodlink.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.imhamid.moodlink.R
import dev.imhamid.moodlink.data.model.Mood
import dev.imhamid.moodlink.ui.components.LanguageSwitch
import dev.imhamid.moodlink.ui.components.MoodField
import dev.imhamid.moodlink.ui.components.MoodFace
import dev.imhamid.moodlink.ui.components.PrimaryButton
import dev.imhamid.moodlink.ui.components.SecondaryButton
import dev.imhamid.moodlink.ui.components.pressable
import dev.imhamid.moodlink.ui.theme.Accent
import dev.imhamid.moodlink.ui.theme.Danger
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft

@Composable
fun AuthScreen(vm: AuthViewModel = hiltViewModel()) {
    BackHandler(enabled = vm.showForm) { vm.back() }

    Column(
        Modifier.fillMaxSize().systemBarsPadding().imePadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.app_name), color = Ink,
                style = MaterialTheme.typography.titleLarge,
            )
            LanguageSwitch()
        }
        AnimatedContent(
            targetState = vm.showForm,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(120)) },
            label = "authContent",
        ) { form -> if (form) FormContent(vm) else WelcomeContent(vm) }
    }
}

@Composable
private fun WelcomeContent(vm: AuthViewModel) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(stringResource(R.string.auth_headline), style = MaterialTheme.typography.displayMedium, color = Ink)
        Text(stringResource(R.string.auth_body), style = MaterialTheme.typography.bodyLarge, color = InkSoft)
        MoodPalette()
        Spacer(Modifier.height(4.dp))
        PrimaryButton(stringResource(R.string.auth_create), onClick = { vm.start(true) })
        SecondaryButton(stringResource(R.string.auth_login), onClick = { vm.start(false) })
    }
}

/** All ten moods, so the first screen shows what the app is actually for. */
@Composable
private fun MoodPalette() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Mood.entries.chunked(5).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { m -> MoodFace(m, Modifier.weight(1f).aspectRatio(1f), squircle = true) }
            }
        }
    }
}

@Composable
private fun FormContent(vm: AuthViewModel) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            Modifier.pressable { vm.back() }.defaultMinSize(minHeight = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = InkSoft)
            Text(stringResource(R.string.back), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        }
        Text(
            stringResource(if (vm.signUpMode) R.string.auth_title_signup else R.string.auth_title_login),
            style = MaterialTheme.typography.headlineMedium, color = Ink,
        )
        if (vm.signUpMode) MoodField(vm.name, vm::onName, stringResource(R.string.field_name))
        MoodField(vm.email, vm::onEmail, stringResource(R.string.field_email), keyboardType = KeyboardType.Email)
        MoodField(vm.password, vm::onPassword, stringResource(R.string.field_password), password = true)
        val err = vm.error
        AnimatedVisibility(visible = err != null, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            Text(err?.let { stringResource(it) }.orEmpty(), color = Danger, style = MaterialTheme.typography.bodyMedium)
        }
        PrimaryButton(
            stringResource(if (vm.signUpMode) R.string.auth_create else R.string.auth_login),
            onClick = vm::submit, enabled = vm.canSubmit, loading = vm.loading,
        )
        if (vm.signUpMode) SwitchLine(stringResource(R.string.auth_have_account), stringResource(R.string.auth_login)) { vm.toggleMode() }
        else SwitchLine(stringResource(R.string.auth_new_here), stringResource(R.string.auth_create)) { vm.toggleMode() }
    }
}

@Composable
private fun SwitchLine(prefix: String, action: String, onClick: () -> Unit) {
    Row(
        Modifier.pressable(onClick = onClick).defaultMinSize(minHeight = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(prefix, color = InkSoft, style = MaterialTheme.typography.bodyMedium)
        Text(action, color = Accent, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
    }
}
