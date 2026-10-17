package com.example.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modern Japanese Minimalist Washi Paper Texture helper.
 * Renders tactile handcrafted Kozo (楮) mulberry fibers and subtle pulp grain
 * without image assets, using high-performance deterministic Canvas operations.
 */

data class WashiFiberSpec(
    val startXRatio: Float,
    val startYRatio: Float,
    val lengthRatio: Float,
    val angleRad: Float,
    val strokeWidth: Float,
    val alphaRatio: Float,
    val curvature: Float
)

// Precomputed deterministic fiber layout for consistent rendering across recompositions
private val PrecomputedWashiFibers = listOf(
    WashiFiberSpec(0.08f, 0.12f, 0.18f, 0.42f, 0.85f, 0.9f, 6f),
    WashiFiberSpec(0.25f, 0.05f, 0.14f, -0.65f, 0.7f, 0.7f, -4f),
    WashiFiberSpec(0.48f, 0.18f, 0.22f, 0.25f, 1.1f, 1.0f, 8f),
    WashiFiberSpec(0.72f, 0.08f, 0.16f, -0.38f, 0.75f, 0.8f, -5f),
    WashiFiberSpec(0.88f, 0.22f, 0.12f, 0.78f, 0.9f, 0.65f, 4f),

    WashiFiberSpec(0.04f, 0.35f, 0.15f, -0.32f, 0.8f, 0.75f, -6f),
    WashiFiberSpec(0.32f, 0.42f, 0.20f, 0.52f, 1.0f, 0.95f, 7f),
    WashiFiberSpec(0.62f, 0.33f, 0.17f, -0.45f, 0.65f, 0.7f, -5f),
    WashiFiberSpec(0.82f, 0.48f, 0.19f, 0.15f, 0.95f, 0.85f, 6f),

    WashiFiberSpec(0.12f, 0.65f, 0.21f, 0.35f, 1.05f, 0.9f, 9f),
    WashiFiberSpec(0.38f, 0.58f, 0.13f, -0.72f, 0.7f, 0.65f, -4f),
    WashiFiberSpec(0.55f, 0.72f, 0.18f, 0.22f, 0.85f, 0.8f, 5f),
    WashiFiberSpec(0.78f, 0.68f, 0.16f, -0.58f, 0.9f, 0.75f, -7f),

    WashiFiberSpec(0.18f, 0.88f, 0.14f, -0.25f, 0.75f, 0.7f, -5f),
    WashiFiberSpec(0.42f, 0.82f, 0.22f, 0.48f, 1.0f, 0.9f, 8f),
    WashiFiberSpec(0.68f, 0.92f, 0.17f, -0.35f, 0.8f, 0.8f, -6f),
    WashiFiberSpec(0.90f, 0.84f, 0.15f, 0.62f, 0.7f, 0.6f, 4f)
)

/**
 * Modifier that applies the tactile Japanese Washi paper grain and organic mulberry fibers.
 */
fun Modifier.washiPaperTexture(
    isDark: Boolean,
    enabled: Boolean = true,
    fiberBaseAlpha: Float = 0.07f
): Modifier = if (!enabled) this else this.drawBehind {
    drawWashiPaperDetails(
        isDark = isDark,
        baseAlpha = fiberBaseAlpha
    )
}

/**
 * Custom draw function for Washi paper fibers and pulp grain.
 */
fun DrawScope.drawWashiPaperDetails(
    isDark: Boolean,
    baseAlpha: Float
) {
    val fiberColor = if (isDark) WashiFiberDark else WashiFiberLight
    val w = size.width
    val h = size.height

    // 1. Subtle warm organic washi paper gradient overlay
    val gradientBrush = Brush.verticalGradient(
        colors = if (isDark) listOf(
            Color(0xFF1B1917).copy(alpha = 0.4f),
            Color(0xFF141210).copy(alpha = 0.6f)
        ) else listOf(
            Color(0xFFFFFDF9).copy(alpha = 0.5f),
            Color(0xFFF5EFE3).copy(alpha = 0.5f)
        )
    )
    drawRect(brush = gradientBrush)

    // 2. Mulberry Kozo fibers
    for (fiber in PrecomputedWashiFibers) {
        val startX = fiber.startXRatio * w
        val startY = fiber.startYRatio * h
        val len = fiber.lengthRatio * (w.coerceAtMost(h) * 0.8f + 60f)
        val endX = startX + len * cos(fiber.angleRad)
        val endY = startY + len * sin(fiber.angleRad)

        val midX = (startX + endX) / 2f + fiber.curvature
        val midY = (startY + endY) / 2f - fiber.curvature

        val path = Path().apply {
            moveTo(startX, startY)
            quadraticTo(midX, midY, endX, endY)
        }

        drawPath(
            path = path,
            color = fiberColor.copy(alpha = (baseAlpha * fiber.alphaRatio).coerceIn(0.01f, 0.25f)),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = fiber.strokeWidth,
                cap = StrokeCap.Round
            )
        )

        // Delicate pulp flecks at fiber terminals
        drawCircle(
            color = fiberColor.copy(alpha = (baseAlpha * 1.2f).coerceIn(0.01f, 0.3f)),
            radius = fiber.strokeWidth * 0.9f,
            center = Offset(startX, startY)
        )
    }
}

/**
 * Traditional Japanese Crimson Vermilion Seal (落款印 • Rakkan-in) stamp.
 * Adds authentic Japanese minimalist artisan signature aesthetic.
 */
@Composable
fun JapaneseCrimsonSeal(
    symbol: String = "和",
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = VermilionRed.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, VermilionRed.copy(alpha = 0.85f)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol,
                color = VermilionRed,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                letterSpacing = 0.sp
            )
        }
    }
}
