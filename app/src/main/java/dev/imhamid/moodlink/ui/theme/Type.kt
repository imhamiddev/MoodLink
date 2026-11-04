package dev.imhamid.moodlink.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MoodTypography = Typography(
    displayMedium = TextStyle(FontFamily.SansSerif, FontWeight.ExtraBold, 40.sp, lineHeight = 46.sp),
    headlineMedium = TextStyle(FontFamily.SansSerif, FontWeight.Bold, 28.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(FontFamily.SansSerif, FontWeight.Bold, 22.sp),
    titleMedium = TextStyle(FontFamily.SansSerif, FontWeight.SemiBold, 17.sp),
    bodyLarge = TextStyle(FontFamily.SansSerif, FontWeight.Normal, 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(FontFamily.SansSerif, FontWeight.Normal, 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(FontFamily.SansSerif, FontWeight.SemiBold, 15.sp),
)
