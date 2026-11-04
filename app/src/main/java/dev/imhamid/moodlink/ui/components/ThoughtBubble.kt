package dev.imhamid.moodlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.imhamid.moodlink.ui.theme.InkSoft

/** Cloud-style speech bubble with two small trailing circles, as in the references. */
@Composable
fun ThoughtBubble(text: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(28.dp)
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.clip(shape).background(Color.White.copy(alpha = .6f))
                .border(1.dp, Color.White.copy(alpha = .9f), shape)
                .padding(horizontal = 22.dp, vertical = 12.dp),
        ) {
            Text(text, style = MaterialTheme.typography.bodyMedium, color = InkSoft, textAlign = TextAlign.Center)
        }
        Row(Modifier.fillMaxWidth().padding(end = 56.dp, top = 4.dp), horizontalArrangement = Arrangement.End) {
            Box(Modifier.size(14.dp).clip(CircleShape).background(Color.White.copy(alpha = .6f)))
            Spacer(Modifier.width(4.dp))
            Box(Modifier.padding(top = 10.dp).size(8.dp).clip(CircleShape).background(Color.White.copy(alpha = .6f)))
        }
    }
}
