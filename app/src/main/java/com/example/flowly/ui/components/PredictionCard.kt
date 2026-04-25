package com.example.flowly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flowly.ui.theme.DangerRed
import com.example.flowly.ui.theme.DarkCard
import com.example.flowly.ui.theme.PrimaryPurple
import com.example.flowly.ui.theme.PrimaryPurpleLight
import com.example.flowly.ui.theme.SafeGreen
import com.example.flowly.ui.theme.TextSecondary
import com.example.flowly.ui.theme.WarningAmber
import com.example.flowly.util.SpendingStatus

@Composable
fun PredictionCard(
    predictedBalance: Double,
    status: SpendingStatus,
    modifier: Modifier = Modifier,
    label: String = "Predicted End-of-Month Balance"
) {
    val statusColor = when (status) {
        SpendingStatus.SAFE -> SafeGreen
        SpendingStatus.WARNING -> WarningAmber
        SpendingStatus.DANGER -> DangerRed
    }

    val statusText = when (status) {
        SpendingStatus.SAFE -> "You're on track! 🎯"
        SpendingStatus.WARNING -> "Getting tight ⚠️"
        SpendingStatus.DANGER -> "Over budget! 🚨"
    }

    val gradientColors = when (status) {
        SpendingStatus.SAFE -> listOf(PrimaryPurple.copy(alpha = 0.15f), SafeGreen.copy(alpha = 0.08f))
        SpendingStatus.WARNING -> listOf(PrimaryPurple.copy(alpha = 0.15f), WarningAmber.copy(alpha = 0.08f))
        SpendingStatus.DANGER -> listOf(DangerRed.copy(alpha = 0.15f), DangerRed.copy(alpha = 0.05f))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(gradientColors)
            )
            .background(DarkCard.copy(alpha = 0.7f))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            AnimatedCounter(
                value = predictedBalance,
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = statusColor,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Text(
                    text = "  $statusText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = valueColor,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
