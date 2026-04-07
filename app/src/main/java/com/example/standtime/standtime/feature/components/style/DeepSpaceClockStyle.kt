package com.example.standtime.standtime.feature.components.style

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.standtime.standtime.feature.components.GalleryClockParts
import com.example.standtime.standtime.feature.utils.StandTimeLanguage
import kotlin.math.sin
import kotlin.random.Random

private data class Star(val x: Float, val y: Float, val r: Float, val phase: Float, val speed: Float)

@Composable
fun DeepSpaceClockStyle(
    parts: GalleryClockParts,
    language: StandTimeLanguage,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "space")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "t"
    )

    val stars = remember {
        List(120) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                r = Random.nextFloat() * 1.4f,
                phase = Random.nextFloat() * (2 * Math.PI).toFloat(),
                speed = Random.nextFloat() * 0.04f + 0.01f
            )
        }
    }

    val nebulaColors = listOf(
        Triple(0.2f, 0.4f, Color(0xFF3296FF).copy(alpha = 0.10f)),
        Triple(0.5f, 0.4f, Color(0xFF8844FF).copy(alpha = 0.10f)),
        Triple(0.8f, 0.4f, Color(0xFF22CC88).copy(alpha = 0.08f))
    )

    Box(
        modifier = modifier.background(Color(0xFF02020F)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Nebula clouds
            for ((nx, ny, col) in nebulaColors) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(col, Color.Transparent),
                        center = Offset(size.width * nx, size.height * ny),
                        radius = size.width * 0.22f
                    ),
                    radius = size.width * 0.22f,
                    center = Offset(size.width * nx, size.height * ny)
                )
            }

            // Stars
            for (star in stars) {
                val alpha = (0.3f + 0.5f * sin(t * star.speed * 60f + star.phase)).coerceIn(0f, 1f)
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = star.r,
                    center = Offset(star.x * size.width, star.y * size.height)
                )
            }
        }

        Text(
            text = "${parts.hours}:${parts.minutes}:${parts.seconds}",
            style = TextStyle(
                color = Color(0xFFDDDDFF),
                fontSize = 122.sp,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(color = Color(0xFF8888FF), blurRadius = 24f)
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF02020F, widthDp = 800, heightDp = 360)
@Composable
private fun DeepSpaceClockStylePreview() = ClockStylePreviewFrame { modifier ->
    DeepSpaceClockStyle(ClockStylePreviewParts, StandTimeLanguage.ENGLISH, ClockStylePreviewAccent, modifier)
}
