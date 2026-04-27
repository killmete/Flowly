package com.example.flowly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.flowly.ui.theme.DangerRed
import com.example.flowly.ui.theme.DarkCard
import com.example.flowly.ui.theme.DarkCardElevated
import com.example.flowly.ui.theme.PrimaryPurple
import com.example.flowly.ui.theme.SafeGreen
import com.example.flowly.ui.theme.TextMuted
import com.example.flowly.ui.theme.TextSecondary
import com.example.flowly.ui.theme.WarningAmber
import com.example.flowly.util.SpendingStatus
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DayCard(
    dayNumber: Int,
    dayOfWeek: String,
    totalSpent: Double,
    predictedEnd: Double,
    status: SpendingStatus,
    isSelected: Boolean,
    isToday: Boolean,
    isFuture: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    val bgColor = when {
        isSelected -> PrimaryPurple.copy(alpha = 0.3f)
        isToday -> PrimaryPurple.copy(alpha = 0.15f)
        isFuture -> DarkCard.copy(alpha = 0.3f)
        else -> DarkCard
    }

    val borderColor = when {
        isSelected -> PrimaryPurple
        isToday -> PrimaryPurple.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    val statusColor = when (status) {
        SpendingStatus.SAFE -> SafeGreen
        SpendingStatus.WARNING -> WarningAmber
        SpendingStatus.DANGER -> DangerRed
    }

    Column(
        modifier = modifier
            .width(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(enabled = !isFuture) { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = dayOfWeek,
            style = MaterialTheme.typography.labelSmall,
            color = if (isFuture) TextMuted else TextSecondary
        )
        Text(
            text = "$dayNumber",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isFuture) TextMuted else MaterialTheme.colorScheme.onSurface
        )
        if (!isFuture) {
            Text(
                text = "$${formatter.format(totalSpent)}",
                style = MaterialTheme.typography.labelSmall,
                color = if (totalSpent > 0) statusColor else TextMuted,
                textAlign = TextAlign.Center
            )
            val absEnd = kotlin.math.abs(predictedEnd)
            val sign = if (predictedEnd < 0) "-" else ""
            Text(
                text = "$sign$${formatter.format(absEnd)}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = statusColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
