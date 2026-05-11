package com.vcam.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font as GoogleFontFont
import androidx.compose.ui.unit.sp
import com.vcam.R

private val GoogleFontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val InstrumentSerifGF = GoogleFont("Instrument Serif")
private val DmSansGF = GoogleFont("DM Sans")
private val JetBrainsMonoGF = GoogleFont("JetBrains Mono")

val InstrumentSerif: FontFamily = FontFamily(
    GoogleFontFont(InstrumentSerifGF, GoogleFontsProvider, FontWeight.Normal),
    GoogleFontFont(InstrumentSerifGF, GoogleFontsProvider, FontWeight.Normal, FontStyle.Italic),
)

val DmSans: FontFamily = FontFamily(
    GoogleFontFont(DmSansGF, GoogleFontsProvider, FontWeight.Normal),
    GoogleFontFont(DmSansGF, GoogleFontsProvider, FontWeight.Medium),
    GoogleFontFont(DmSansGF, GoogleFontsProvider, FontWeight.SemiBold),
    GoogleFontFont(DmSansGF, GoogleFontsProvider, FontWeight.Bold),
)

val JetBrainsMono: FontFamily = FontFamily(
    GoogleFontFont(JetBrainsMonoGF, GoogleFontsProvider, FontWeight.Normal),
    GoogleFontFont(JetBrainsMonoGF, GoogleFontsProvider, FontWeight.Medium),
)

object VType {
    val HeroDisplayLarge = TextStyle(
        fontFamily = InstrumentSerif,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 33.sp,
    )
    val HeroDisplay = TextStyle(
        fontFamily = InstrumentSerif,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 28.6.sp,
    )
    val HeroDisplaySmall = TextStyle(
        fontFamily = InstrumentSerif,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 23.1.sp,
    )
    val SectionHeader = TextStyle(
        fontFamily = InstrumentSerif,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 24.sp,
    )
    val Body = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    )
    val BodySemi = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
    )
    val SecondaryLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
    )
    val Secondary = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.5.sp,
    )
    val SecondarySmall = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    )
    val Caption = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
    )
    val Mono = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 1.4.sp,
    )
    val MonoLarge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 1.6.sp,
    )
    val MonoXSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 9.sp,
        letterSpacing = 0.6.sp,
    )
    val MonoValue = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
    )
}

val VTypography: Typography = Typography(
    displayLarge = VType.HeroDisplayLarge,
    displayMedium = VType.HeroDisplay,
    displaySmall = VType.HeroDisplaySmall,
    titleLarge = VType.SectionHeader,
    bodyLarge = VType.Body,
    bodyMedium = VType.SecondaryLarge,
    bodySmall = VType.SecondarySmall,
    labelLarge = VType.BodySemi,
    labelMedium = VType.SecondarySmall,
    labelSmall = VType.Mono,
)
