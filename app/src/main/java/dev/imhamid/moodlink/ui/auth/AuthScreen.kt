package dev.imhamid.moodlink.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.imhamid.moodlink.ui.components.*
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft
import dev.imhamid.moodlink.ui.theme.Purple600

@Composable
fun AuthScreen(vm: AuthViewModel = hiltViewModel()) {
    BackHandler(enabled = vm.showForm) { vm.back() }
    val mascotScale by animateFloatAsState(
        if (vm.showForm) .8f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "mascotScale",
    )

    Column(Modifier.fillMaxSize().statusBarsPadding().imePadding()) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            ThoughtBubble(
                "Daily moods, shared with the people you love",
                Modifier.align(Alignment.TopCenter).padding(top = 16.dp, start = 40.dp, end = 40.dp),
            )
            Mascot(
                Modifier.padding(top = 84.dp, bottom = 8.dp)
                    .heightIn(max = 300.dp).fillMaxHeight()
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .graphicsLayer { scaleX = mascotScale; scaleY = mascotScale },
            )
        }

        Surface(
            modifier = Modifier.animateContentSize(spring(dampingRatio = .8f, stiffness = Spring.StiffnessMediumLow)),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White.copy(alpha = .66f),
        ) {
            Column(
                Modifier.fillMaxWidth().navigationBarsPadding()
                    .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                AnimatedContent(
                    targetState = vm.showForm,
                    transitionSpec = {
                        val dir = if (targetState) 1 else -1
                        (slideInHorizontally(tween(380)) { dir * it / 3 } + fadeIn(tween(380))) togetherWith
                            (slideOutHorizontally(tween(260)) { -dir * it / 3 } + fadeOut(tween(180)))
                    },
                    label = "authContent",
                ) { form ->
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) { if (form) FormContent(vm) else WelcomeContent(vm) }
                }
            }
        }
    }
}

@Composable
private fun WelcomeContent(vm: AuthViewModel) {
    Text("MoodLink", color = Purple600, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
    Text(
        "Feel it. Share it.\nStay close.",
        style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, color = Ink,
    )
    Text(
        "Let the people you love see how you feel, right on their home screen.",
        style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = InkSoft,
    )
    Spacer(Modifier.height(4.dp))
    GradientButton("Get Started", onClick = { vm.start(true) })
    SwitchLine("Already have an account? ", "Log In") { vm.start(false) }
}

@Composable
private fun FormContent(vm: AuthViewModel) {
    Text(
        if (vm.signUpMode) "Create your account" else "Welcome back",
        style = MaterialTheme.typography.headlineMedium, color = Ink,
    )
    if (vm.signUpMode) MoodField(vm.name, vm::onName, "Your name")
    MoodField(vm.email, vm::onEmail, "Email", keyboardType = KeyboardType.Email)
    MoodField(vm.password, vm::onPassword, "Password (6+ characters)", password = true)
    val err = vm.error
    AnimatedVisibility(visible = err != null, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
        Text(err.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    }
    GradientButton(
        if (vm.signUpMode) "Create account" else "Log in",
        onClick = vm::submit, enabled = vm.canSubmit, loading = vm.loading,
    )
    if (vm.signUpMode) SwitchLine("Already have an account? ", "Log In") { vm.toggleMode() }
    else SwitchLine("New here? ", "Sign Up") { vm.toggleMode() }
    Text("Back", color = InkSoft, modifier = Modifier.bounceClick { vm.back() }.padding(8.dp))
}

@Composable
private fun SwitchLine(prefix: String, action: String, onClick: () -> Unit) {
    Row(Modifier.bounceClick(onClick = onClick).padding(8.dp)) {
        Text(prefix, color = InkSoft, style = MaterialTheme.typography.bodyMedium)
        Text(action, color = Ink, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
    }
}
