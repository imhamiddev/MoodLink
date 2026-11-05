package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.imhamid.moodlink.ui.theme.Accent
import dev.imhamid.moodlink.ui.theme.ControlShape
import dev.imhamid.moodlink.ui.theme.Ink
import dev.imhamid.moodlink.ui.theme.InkSoft
import dev.imhamid.moodlink.ui.theme.Line
import dev.imhamid.moodlink.ui.theme.Raised

@Composable
fun MoodField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        shape = ControlShape,
        keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else keyboardType),
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Raised,
            unfocusedContainerColor = Raised,
            focusedBorderColor = Accent,
            unfocusedBorderColor = Line,
            focusedLabelColor = Accent,
            unfocusedLabelColor = InkSoft,
            focusedTextColor = Ink,
            unfocusedTextColor = Ink,
            cursorColor = Accent,
        ),
    )
}
