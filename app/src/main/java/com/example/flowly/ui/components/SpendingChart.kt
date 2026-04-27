package com.example.flowly.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.flowly.ui.theme.ChartGradientEnd
import com.example.flowly.ui.theme.ChartGradientStart
import com.example.flowly.ui.theme.DangerRed
import com.example.flowly.ui.theme.TextMuted

@Composable
fun SpendingChart(
    dataPoints: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = ChartGradientStart,
    fillGradientTop: Color = ChartGradientStart.copy(alpha = 0.3f),
    fillGradientBottom: Color = ChartGradientEnd.copy(alpha = 0.02f),
    zeroLineColor: Color = DangerRed.copy(alpha = 0.4f),
    gridColor: Color = TextMuted.copy(alpha = 0.2f)
) {
    if (dataPoints.isEmpty()) return

    var animProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(dataPoints) {
        animProgress = 0f
        kotlinx.coroutines.yield()
        animProgress = 1f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = animProgress,
        animationSpec = tween(durationMillis = 1500),
        label = "chartAnim"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        val w = size.width
        val h = size.height
        val padding = 16f

        val maxVal = dataPoints.max().coerceAtLeast(1.0)
        val minVal = dataPoints.min().coerceAtMost(0.0)
        val range = (maxVal - minVal).coerceAtLeast(1.0)

        val chartW = w - padding * 2
        val chartH = h - padding * 2

        // Draw horizontal grid lines
        for (i in 0..4) {
            val y = padding + chartH * i / 4f
            drawLine(
                color = gridColor,
                start = Offset(padding, y),
                end = Offset(w - padding, y),
                strokeWidth = 1f
            )
        }

        // Draw zero line if relevant
        if (minVal < 0) {
            val zeroY = padding + chartH * (1f - (0.0 - minVal).toFloat() / range.toFloat())
            drawLine(
                color = zeroLineColor,
                start = Offset(padding, zeroY),
                end = Offset(w - padding, zeroY),
                strokeWidth = 2f
            )
        }

        if (dataPoints.size < 2) return@Canvas

        val step = chartW / (dataPoints.size - 1).coerceAtLeast(1)

        // Determine how many points to draw based on animation progress
        val visibleCount = (dataPoints.size * animatedProgress).toInt().coerceAtLeast(1)
        val visiblePoints = dataPoints.take(visibleCount)

        // Build line path
        val linePath = Path()
        val fillPath = Path()

        visiblePoints.forEachIndexed { index, value ->
            val x = padding + index * step
            val y = padding + chartH * (1f - (value - minVal).toFloat() / range.toFloat())

            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, h - padding)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        // Close fill path
        val lastX = padding + (visiblePoints.size - 1) * step
        fillPath.lineTo(lastX, h - padding)
        fillPath.close()

        // Draw gradient fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(fillGradientTop, fillGradientBottom),
                startY = 0f,
                endY = h
            )
        )

        // Draw the line
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(
                width = 3f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw dots
        visiblePoints.forEachIndexed { index, value ->
            val x = padding + index * step
            val y = padding + chartH * (1f - (value - minVal).toFloat() / range.toFloat())

            // Outer glow
            drawCircle(
                color = lineColor.copy(alpha = 0.3f),
                radius = 6f,
                center = Offset(x, y)
            )
            // Inner dot
            drawCircle(
                color = lineColor,
                radius = 3.5f,
                center = Offset(x, y)
            )
        }
    }
}
