package dev.imhamid.moodlink.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.imhamid.moodlink.R

/** Vazirmatn (SIL OFL) for Persian. It also carries Latin glyphs, so mixed text stays consistent. */
val VazirmatnFamily = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
)

/**
 * English: serif for display lines, sans for text. Persian: Vazirmatn everywhere, because the
 * system fonts for Arabic script are not designed for large display sizes.
 * Steps: 12 / 14 / 16 / 22 / 32 / 40.
 */
fun moodTypography(persian: Boolean): Typography {
    val display = if (persian) VazirmatnFamily else FontFamily.Serif
    val text = if (persian) VazirmatnFamily else FontFamily.SansSerif
    val tall = if (persian) 1.2f else 1f   // Arabic script needs a little more line height
    fun s(size: Int, line: Int, weight: FontWeight, family: FontFamily) =
        TextStyle(fontFamily = family, fontWeight = weight, fontSize = size.sp, lineHeight = (line * tall).sp)
    return Typography(
        displayMedium = s(40, 46, FontWeight.Bold, display),
        headlineMedium = s(32, 38, FontWeight.Bold, display),
        titleLarge = s(22, 28, FontWeight.Bold, text),
        titleMedium = s(16, 22, FontWeight.SemiBold, text),
        bodyLarge = s(16, 24, FontWeight.Normal, text),
        bodyMedium = s(14, 20, FontWeight.Normal, text),
        bodySmall = s(12, 16, FontWeight.Normal, text),
        labelLarge = s(16, 22, FontWeight.SemiBold, text),
    )
}
