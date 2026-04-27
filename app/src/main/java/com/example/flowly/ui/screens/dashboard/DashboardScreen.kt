package com.example.flowly.ui.screens.dashboard

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
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
import com.example.flowly.ui.components.BudgetProgressBar
import com.example.flowly.ui.components.ExpenseItem
import com.example.flowly.ui.components.PredictionCard
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
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    if (!state.hasBudget && !state.isLoading) {
        // No budget set up — show onboarding prompt
        NoBudgetPrompt(onSetup = onNavigateToSettings)
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { -40 }
            ) {
                Column {
                    Text(
                        text = DateUtils.formatMonthYearForDisplay(DateUtils.currentMonthYear()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Prediction Card
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { 60 }
            ) {
                state.prediction?.let { pred ->
                    PredictionCard(
                        predictedBalance = pred.predictedEndBalance,
                        status = pred.status
                    )
                }
            }
        }

        // Budget Progress Arc + Stats Row
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { 80 }
            ) {
                val pred = state.prediction
                val budget = state.budget
                if (pred != null && budget != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BudgetProgressBar(
                            spent = pred.totalSpent,
                            total = budget.usableBudget,
                            size = 140.dp,
                            strokeWidth = 12.dp
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Days Left",
                                value = "${pred.daysLeft}",
                                valueColor = PrimaryPurple,
                                modifier = Modifier.fillMaxWidth()
                            )
                            StatCard(
                                title = "Safe/Day",
                                value = "$${formatter.format(pred.safeDailyBudget)}",
                                valueColor = when (pred.status) {
                                    SpendingStatus.SAFE -> SafeGreen
                                    SpendingStatus.WARNING -> WarningAmber
                                    SpendingStatus.DANGER -> DangerRed
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // Quick stats
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { 100 }
            ) {
                val pred = state.prediction
                if (pred != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total Spent",
                            value = "$${formatter.format(pred.totalSpent)}",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Remaining",
                            value = "$${formatter.format(pred.remaining)}",
                            valueColor = if (pred.remaining >= 0) SafeGreen else DangerRed,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Today",
                            value = "$${formatter.format(state.todayTotal)}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Spending Chart
        item {
            AnimatedVisibility(
                visible = visible && state.chartData.isNotEmpty(),
                enter = fadeIn() + slideInVertically { 120 }
            ) {
                Column(
                    modifier = Modifier
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
                            imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Prediction Trend",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    SpendingChart(dataPoints = state.chartData)
                }
            }
        }

        // Today's Expenses Header
        item {
            AnimatedVisibility(
                visible = visible && state.todayExpenses.isNotEmpty(),
                enter = fadeIn() + slideInVertically { 140 }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Expenses",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${state.todayExpenses.size} items",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Today's expense list
        items(state.todayExpenses, key = { it.id }) { expense ->
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { 160 }
            ) {
                ExpenseItem(
                    expense = expense,
                    onDelete = { viewModel.deleteExpense(expense) }
                )
            }
        }

        // Bottom spacing for nav bar
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun NoBudgetPrompt(onSetup: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.AccountBalanceWallet,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "Welcome to Flowly!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Set up your monthly budget to start predicting your finances.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            androidx.compose.material3.Button(
                onClick = onSetup,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Set Up Budget",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
