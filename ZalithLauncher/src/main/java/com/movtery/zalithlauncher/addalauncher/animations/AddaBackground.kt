package com.movtery.zalithlauncher.addalauncher.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.movtery.zalithlauncher.addalauncher.studio.AddaStudio
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private class Particle(val x: Float, val y: Float, val speed: Float, val size: Float, val phase: Float)

/**
 * Animated AddaLauncher backdrop: gradient base, moving lights and floating particles.
 * Honors Studio settings (performance mode, reduced animations/particles).
 * Uses a single Canvas and one infinite transition; no bitmaps are allocated.
 */
@Composable
fun AddaBackground(modifier: Modifier = Modifier.fillMaxSize()) {
    val primary = AddaStudio.primary
    val secondary = AddaStudio.secondary
    val accent = AddaStudio.accent
    val count = AddaStudio.effectiveParticles()
    val lightCount = if (AddaStudio.movingLightsOn) AddaStudio.perfMode.lights else 0
    val animate = !AddaStudio.reducedAnimations
    val particles = remember(count) {
        val r = Random(7)
        List(count) { Particle(r.nextFloat(), r.nextFloat(), 0.2f + r.nextFloat() * 0.8f, 1.5f + r.nextFloat() * 3f, r.nextFloat()) }
    }
    val t = if (animate) {
        val tr = rememberInfiniteTransition(label = "adda_bg")
        val v by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart), label = "t")
        v
    } else 0.15f

    Canvas(modifier) {
        drawRect(Brush.linearGradient(listOf(Color(0xFF07040F), primary.copy(alpha = 0.35f).compositeOver(Color(0xFF07040F)), Color(0xFF020611))))
        val lightColors = listOf(primary, secondary, accent, primary)
        for (i in 0 until lightCount) {
            val a = (t + i * 0.25f) * 2f * PI.toFloat()
            val cx = size.width * (0.5f + 0.38f * cos(a + i))
            val cy = size.height * (0.5f + 0.35f * sin(a * 0.8f + i * 2))
            val radius = size.minDimension * (0.55f + 0.1f * i)
            drawCircle(
                Brush.radialGradient(listOf(lightColors[i % 4].copy(alpha = 0.28f), Color.Transparent), Offset(cx, cy), radius),
                radius, Offset(cx, cy)
            )
        }
        for (p in particles) {
            val yy = ((p.y - t * p.speed * 2f) % 1f + 1f) % 1f
            val xx = p.x + 0.02f * sin((t * 6f + p.phase) * 2f * PI.toFloat())
            val alpha = 0.25f + 0.5f * (0.5f + 0.5f * sin((t * 8f + p.phase * 6f)))
            drawCircle(Color.White.copy(alpha = alpha), p.size, Offset(xx * size.width, yy * size.height))
        }
    }
}

private fun Color.compositeOver(bg: Color): Color {
    val a = alpha
    return Color(red * a + bg.red * (1 - a), green * a + bg.green * (1 - a), blue * a + bg.blue * (1 - a), 1f)
}
