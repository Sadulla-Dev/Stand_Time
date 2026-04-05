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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.standtime.standtime.feature.components.GalleryClockParts
import com.example.standtime.standtime.feature.utils.StandTimeLanguage
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val x: Float, val y: Float,
    val vx: Float, val vy: Float,
    val r: Float, val hue: Float
)

@Composable
fun QuantumClockStyle(
    parts: GalleryClockParts,
    language: StandTimeLanguage,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "quantum")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "t"
    )

    Box(
        modifier = modifier.background(Color(0xFF05000F)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Orbital rings (3 elliptical rings)
            for (i in 0..2) {
                val ringRadius = size.width * 0.22f + i * 20f
                val scaleY = 0.35f + i * 0.15f
                val rotSpeed = t * (i + 1).toFloat() * 0.4f + i * (Math.PI / 3.0).toFloat()
                val ringHue = 260f + i * 20f
                val ringColor = Color.hsv(ringHue, 0.8f, 0.9f).copy(alpha = 0.35f)

                // Draw ellipse as multiple points
                val steps = 60
                for (s in 0 until steps) {
                    val angle = s * (2f * Math.PI / steps).toFloat()
                    val nextAngle = (s + 1) * (2f * Math.PI / steps).toFloat()
                    val cosR = cos(rotSpeed)
                    val sinR = sin(rotSpeed)

                    val x1 = cos(angle) * ringRadius
                    val y1 = sin(angle) * ringRadius * scaleY
                    val rx1 = cx + x1 * cosR - y1 * sinR
                    val ry1 = cy + x1 * sinR + y1 * cosR

                    val x2 = cos(nextAngle) * ringRadius
                    val y2 = sin(nextAngle) * ringRadius * scaleY
                    val rx2 = cx + x2 * cosR - y2 * sinR
                    val ry2 = cy + x2 * sinR + y2 * cosR

                    drawLine(ringColor, Offset(rx1, ry1), Offset(rx2, ry2), strokeWidth = 1.2f)
                }

                // Orbiting dot
                val dotAngle = t * (i + 1).toFloat() * 0.9f
                val dotX = cos(dotAngle) * ringRadius
                val dotY = sin(dotAngle) * ringRadius * scaleY
                val cosR = cos(rotSpeed)
                val sinR = sin(rotSpeed)
                val rdx = cx + dotX * cosR - dotY * sinR
                val rdy = cy + dotX * sinR + dotY * cosR
                drawCircle(
                    color = Color.hsv((260f + i * 30f), 1f, 1f),
                    radius = 5f,
                    center = Offset(rdx, rdy)
                )
            }

            // Floating particles
            val particleCount = 30
            for (p in 0 until particleCount) {
                val px = (p * 73.13f + t * 20f) % size.width
                val py = (p * 47.71f + t * 15f) % size.height
                drawCircle(
                    color = Color.hsv(260f + p % 60f, 0.8f, 0.9f, alpha = 0.5f),
                    radius = 1.5f + (p % 3) * 0.5f,
                    center = Offset(px, py)
                )
            }
        }

        Text(
            text = "${parts.hours}:${parts.minutes}:${parts.seconds}",
            style = TextStyle(
                color = Color.White,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(color = Color(0xFFCC88FF), blurRadius = 24f)
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF05000F, widthDp = 800, heightDp = 360)
@Composable
private fun QuantumClockStylePreview() = ClockStylePreviewFrame { modifier ->
    QuantumClockStyle(ClockStylePreviewParts, StandTimeLanguage.ENGLISH, ClockStylePreviewAccent, modifier)
}
