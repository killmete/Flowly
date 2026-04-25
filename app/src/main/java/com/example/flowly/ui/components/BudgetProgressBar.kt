package com.example.flowly.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.flowly.ui.theme.DangerRed
import com.example.flowly.ui.theme.DarkCard
import com.example.flowly.ui.theme.SafeGreen
import com.example.flowly.ui.theme.WarningAmber

@Composable
fun BudgetProgressBar(
    spent: Double,
    total: Double,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    strokeWidth: Dp = 14.dp,
    label: String = "Spent"
) {
    val progress = if (total > 0) (spent / total).toFloat().coerceIn(0f, 1.5f) else 0f

    var animTarget by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(progress) { animTarget = progress }

    val animatedProgress by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = tween(durationMillis = 1200),
        label = "progress"
    )

    val progressColor = when {
        progress >= 1f -> DangerRed
        progress >= 0.75f -> WarningAmber
        else -> SafeGreen
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(stroke / 2, stroke / 2)

            // Background track
            drawArc(
                color = DarkCard,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress arc
            val sweep = (animatedProgress.coerceAtMost(1f) * 270f)
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(progressColor.copy(alpha = 0.6f), progressColor)
                ),
                startAngle = 135f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${(animatedProgress * 100).toInt().coerceAtMost(150)}%",
                style = MaterialTheme.typography.headlineMedium,
                color = progressColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8B949E)
            )
        }
    }
}
