package com.example.flowly.ui.screens.timeline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flowly.ui.components.DayCard
import com.example.flowly.ui.components.ExpenseItem
import com.example.flowly.ui.components.SpendingChart
import com.example.flowly.ui.components.StatCard
import com.example.flowly.ui.theme.DangerRed
import com.example.flowly.ui.theme.DarkCard
import com.example.flowly.ui.theme.PrimaryPurple
import com.example.flowly.ui.theme.SafeGreen
import com.example.flowly.ui.theme.TextSecondary
import com.example.flowly.ui.theme.WarningAmber
import com.example.flowly.util.DateUtils
import com.example.flowly.util.SpendingStatus
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    val daysPassed = DateUtils.getDaysPassedInMonth()
    val dayListState = rememberLazyListState()

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    // Scroll to today on first load
    LaunchedEffect(state.days) {
        if (state.days.isNotEmpty()) {
            val scrollIndex = (daysPassed - 1).coerceAtLeast(0)
            dayListState.animateScrollToItem(scrollIndex.coerceAtMost(state.days.size - 1))
        }
    }

    if (!state.hasBudget && !state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.Timeline,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Set up a budget first",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { -40 }
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = DateUtils.formatMonthYearForDisplay(DateUtils.currentMonthYear()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Timeline",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Day cards horizontal scroll
        item {
            AnimatedVisibility(
                visible = visible && state.days.isNotEmpty(),
                enter = fadeIn() + slideInVertically { 60 }
            ) {
                LazyRow(
                    state = dayListState,
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.days) { snapshot ->
                        val isFuture = snapshot.day > daysPassed
                        DayCard(
                            dayNumber = snapshot.day,
                            dayOfWeek = snapshot.dayOfWeek,
                            totalSpent = snapshot.dailySpent,
                            predictedEnd = snapshot.predictedEndBalance,
                            status = snapshot.status,
                            isSelected = snapshot.day == state.selectedDay,
                            isToday = snapshot.day == daysPassed,
                            isFuture = isFuture,
                            onClick = { viewModel.selectDay(snapshot.day) }
                        )
                    }
                }
            }
        }

        // Prediction Chart
        item {
            AnimatedVisibility(
                visible = visible && state.chartData.isNotEmpty(),
                enter = fadeIn() + slideInVertically { 80 }
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkCard)
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Timeline,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Predicted End Balance by Day",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    SpendingChart(dataPoints = state.chartData)
                }
            }
        }

        // Selected day details
        item {
            val selectedSnapshot = state.selectedDay?.let { day ->
                state.days.getOrNull(day - 1)
            }

            if (selectedSnapshot != null && selectedSnapshot.day <= daysPassed) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically { 100 }
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Day ${selectedSnapshot.day} — ${DateUtils.formatDateForDisplay(selectedSnapshot.date)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Spent Today",
                                value = "$${formatter.format(selectedSnapshot.dailySpent)}",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Total Spent",
                                value = "$${formatter.format(selectedSnapshot.totalSpentUpToDay)}",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val statusColor = when (selectedSnapshot.status) {
                                SpendingStatus.SAFE -> SafeGreen
                                SpendingStatus.WARNING -> WarningAmber
                                SpendingStatus.DANGER -> DangerRed
                            }
                            StatCard(
                                title = "Remaining",
                                value = "$${formatter.format(selectedSnapshot.remaining)}",
                                valueColor = if (selectedSnapshot.remaining >= 0) SafeGreen else DangerRed,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Predicted End",
                                value = "$${formatter.format(selectedSnapshot.predictedEndBalance)}",
                                valueColor = statusColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Selected day expenses
        if (state.selectedDayExpenses.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn()
                ) {
                    Text(
                        text = "Expenses on this day",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
            items(state.selectedDayExpenses, key = { it.id }) { expense ->
                ExpenseItem(
                    expense = expense,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        // Bottom spacing
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
