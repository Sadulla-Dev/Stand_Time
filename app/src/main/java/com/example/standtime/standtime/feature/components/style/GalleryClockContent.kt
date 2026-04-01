package com.example.standtime.standtime.feature.components.style

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.standtime.standtime.feature.utils.StandTimeLanguage

@Composable
fun GalleryClockContent(
    index: Int,
    parts: GalleryClockParts,
    language: StandTimeLanguage,
    accentColor: Color,
    modifier: Modifier = Modifier
) {

    val clockStyles = listOf<@Composable (GalleryClockParts, StandTimeLanguage, Color, Modifier) -> Unit>(
        ::NothingOfficialClockStyle,
        ::Ps5ClockStyle,
        ::TeslaClockStyle,
        ::MinecraftClockStyle,
        ::NasaClockStyle,
        ::PixelStackClockStyle,
        ::TokyoClockStyle,
        ::IosStackClockStyle,
        ::BraunClockStyle,
        ::TerminalClockStyle,
        ::CyberpunkClockStyle,
        ::PixelPetClockStyle,
        ::LofiClockStyle,
        ::RolexClockStyle,
        ::GlassClockStyle,
        ::LuxuryClockStyle,
        ::BauhausClockStyle,
        ::MacOsClockStyle,
        ::WordsClockStyle,
        ::CoffeeClockStyle,
        ::NightOwlClockStyle,
        ::ArcadeClockStyle,
        ::AnalogZenClockStyle,
        ::RetroFlipClockStyle,
        ::BinaryPulseClockStyle,
        ::SolarOrbitClockStyle,
        ::TypewriterClockStyle,
        ::LiquidGradientClockStyle,
        ::AdminPanelClockStyle,
        ::PhotoFrameClockStyle,
        ::SynthwaveClockStyle,
        ::ZenArchitectureClockStyle,
        ::ArchitectStudioClockStyle,
        ::OledStealthClockStyle,
        ::NordicClockStyle,
        ::SwissClockStyle,
        ::IndustrialClockStyle,
        ::TokyoNeonClockStyle,
        ::PaperMinimalismClockStyle,
        ::CyberGlitchClockStyle,
        ::AbstractGeometricClockStyle,
        ::TypographyFocusClockStyle,
        ::NothingDotClockStyle
    )

    ResponsiveGalleryFrame(modifier = modifier) {
        clockStyles.getOrNull(index)?.invoke(
            parts,
            language,
            accentColor,
            Modifier.fillMaxSize()
        ) ?: FrostedStudioClockStyle(
            parts,
            language,
            accentColor,
            Modifier.fillMaxSize()
        )
    }
}