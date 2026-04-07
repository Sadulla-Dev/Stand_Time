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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.standtime.standtime.feature.components.GalleryClockParts
import com.example.standtime.standtime.feature.utils.StandTimeLanguage
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ArcticPulseClockStyle(
    parts: GalleryClockParts,
    language: StandTimeLanguage,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "arctic")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "t"
    )

    Box(
        modifier = modifier.background(Color(0xFF000812)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val maxR = min(size.width, size.height) * 0.45f

            // Sonar pulse rings
            for (ring in 0..3) {
                val phase = (t * 0.6f + ring * 0.7f) % (2 * Math.PI).toFloat()
                val r = (phase / (2 * Math.PI).toFloat()) * maxR
                val alpha = (0.7f - (phase / (2 * Math.PI).toFloat()) * 0.7f) * 0.5f
                drawCircle(
                    color = Color(0xFF00DCFF).copy(alpha = alpha),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5f)
                )
            }

            // Radar grid spokes
            for (i in 0..5) {
                val a = i * (Math.PI / 3.0).toFloat()
                drawLine(
                    color = Color(0xFF0096DC).copy(alpha = 0.1f),
                    start = Offset(cx, cy),
                    end = Offset(cx + cos(a) * maxR, cy + sin(a) * maxR),
                    strokeWidth = 0.5f
                )
            }

            // Outer circle
            drawCircle(
                color = Color(0xFF0096DC).copy(alpha = 0.15f),
                radius = maxR,
                center = Offset(cx, cy),
                style = Stroke(width = 0.5f)
            )

            // Rotating scanner sweep
            val scanAngle = t * 0.8f
            val sweepPath = Path()
            sweepPath.moveTo(cx, cy)
            val sweepSteps = 20
            for (s in 0..sweepSteps) {
                val a = scanAngle + s * (Math.PI / 4.0 / sweepSteps).toFloat()
                sweepPath.lineTo(cx + cos(a) * maxR, cy + sin(a) * maxR)
            }
            sweepPath.close()
            drawPath(sweepPath, Color(0xFF00DCFF).copy(alpha = 0.07f), style = Fill)

            // Scanner arm
            drawLine(
                color = Color(0xFF00DCFF).copy(alpha = 0.55f),
                start = Offset(cx, cy),
                end = Offset(cx + cos(scanAngle) * maxR, cy + sin(scanAngle) * maxR),
                strokeWidth = 1.5f
            )
        }

        Text(
            text = "${parts.hours}:${parts.minutes}:${parts.seconds}",
            style = TextStyle(
                color = Color(0xFFCCEEFF),
                fontSize = 122.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                shadow = Shadow(color = Color(0xFF00DDFF), blurRadius = 18f)
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000812, widthDp = 800, heightDp = 360)
@Composable
private fun ArcticPulseClockStylePreview() = ClockStylePreviewFrame { modifier ->
    ArcticPulseClockStyle(ClockStylePreviewParts, StandTimeLanguage.ENGLISH, ClockStylePreviewAccent, modifier)
}
