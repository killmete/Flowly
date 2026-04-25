package com.example.flowly.ui.screens.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.flowly.FlowlyApplication
import com.example.flowly.data.local.entity.Budget
import com.example.flowly.data.local.entity.Expense
import com.example.flowly.data.repository.FlowlyRepository
import com.example.flowly.util.DateUtils
import com.example.flowly.util.PredictionEngine
import com.example.flowly.util.PredictionResult
import com.example.flowly.util.SpendingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DaySnapshot(
    val day: Int,
    val date: String,
    val dayOfWeek: String,
    val totalSpentUpToDay: Double,
    val dailySpent: Double,
    val remaining: Double,
    val predictedEndBalance: Double,
    val status: SpendingStatus,
    val expenses: List<Expense> = emptyList()
)

data class TimelineUiState(
    val days: List<DaySnapshot> = emptyList(),
    val selectedDay: Int? = null,
    val selectedDayExpenses: List<Expense> = emptyList(),
    val chartData: List<Double> = emptyList(),
    val budget: Budget? = null,
    val isLoading: Boolean = true,
    val hasBudget: Boolean = false
)

class TimelineViewModel(private val repository: FlowlyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    init {
        loadTimeline()
    }

    private fun loadTimeline() {
        val monthYear = DateUtils.currentMonthYear()
        val firstDay = DateUtils.getFirstDayOfMonth(monthYear)
        val lastDay = DateUtils.getLastDayOfMonth(monthYear)

        viewModelScope.launch {
            // Combine budget AND expenses flows so timeline rebuilds
            // whenever any expense is added/deleted — including past days
            combine(
                repository.getBudgetForMonth(monthYear),
                repository.getExpensesBetween(firstDay, lastDay)
            ) { budget, allExpenses ->
                Pair(budget, allExpenses)
            }.collect { (budget, allExpenses) ->
                if (budget != null) {
                    buildTimeline(budget, monthYear, allExpenses)
                } else {
                    _uiState.value = TimelineUiState(isLoading = false, hasBudget = false)
                }
            }
        }
    }

    private fun buildTimeline(budget: Budget, monthYear: String, allExpenses: List<Expense>) {
        val totalDays = DateUtils.getDaysInMonth(monthYear)
        val daysPassed = DateUtils.getDaysPassedInMonth()

        // Preserve the currently selected day across reactive updates
        val previousSelectedDay = _uiState.value.selectedDay

        val days = mutableListOf<DaySnapshot>()
        val chartData = mutableListOf<Double>()
        var cumulativeSpent = 0.0

        for (day in 1..totalDays) {
            val date = DateUtils.dateForDay(monthYear, day)
            val dayOfWeek = DateUtils.getDayOfWeek(date)
            val dayExpenses = allExpenses.filter { it.date == date }
            val dailySpent = dayExpenses.sumOf { it.amount }
            cumulativeSpent += dailySpent

            if (day <= daysPassed) {
                val prediction = PredictionEngine.calculateFullPrediction(
                    usableBudget = budget.usableBudget,
                    totalSpent = cumulativeSpent,
                    daysPassed = day,
                    totalDaysInMonth = totalDays
                )

                days.add(
                    DaySnapshot(
                        day = day,
                        date = date,
                        dayOfWeek = dayOfWeek,
                        totalSpentUpToDay = cumulativeSpent,
                        dailySpent = dailySpent,
                        remaining = budget.usableBudget - cumulativeSpent,
                        predictedEndBalance = prediction.predictedEndBalance,
                        status = prediction.status,
                        expenses = dayExpenses
                    )
                )
                chartData.add(prediction.predictedEndBalance)
            } else {
                // Future day
                days.add(
                    DaySnapshot(
                        day = day,
                        date = date,
                        dayOfWeek = dayOfWeek,
                        totalSpentUpToDay = 0.0,
                        dailySpent = 0.0,
                        remaining = 0.0,
                        predictedEndBalance = 0.0,
                        status = SpendingStatus.SAFE
                    )
                )
            }
        }

        val selectedDay = previousSelectedDay ?: daysPassed

        _uiState.value = TimelineUiState(
            days = days,
            selectedDay = selectedDay,
            selectedDayExpenses = days.getOrNull(selectedDay - 1)?.expenses ?: emptyList(),
            chartData = chartData,
            budget = budget,
            isLoading = false,
            hasBudget = true
        )
    }

    fun selectDay(day: Int) {
        val currentState = _uiState.value
        val snapshot = currentState.days.getOrNull(day - 1)
        _uiState.value = currentState.copy(
            selectedDay = day,
            selectedDayExpenses = snapshot?.expenses ?: emptyList()
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlowlyApplication
                return TimelineViewModel(app.repository) as T
            }
        }
    }
}
