package com.example.flowly.ui.screens.dashboard

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

data class DashboardUiState(
    val budget: Budget? = null,
    val prediction: PredictionResult? = null,
    val todayExpenses: List<Expense> = emptyList(),
    val todayTotal: Double = 0.0,
    val chartData: List<Double> = emptyList(),
    val isLoading: Boolean = true,
    val hasBudget: Boolean = false
)

class DashboardViewModel(private val repository: FlowlyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val monthYear = DateUtils.currentMonthYear()
        val today = DateUtils.today()
        val firstDay = DateUtils.getFirstDayOfMonth(monthYear)

        viewModelScope.launch {
            // Combine budget, total spent, and today's expenses into one reactive stream
            combine(
                repository.getBudgetForMonth(monthYear),
                repository.getTotalSpentBetween(firstDay, today),
                repository.getExpensesByDate(today)
            ) { budget, totalSpent, todayExpenses ->
                Triple(budget, totalSpent, todayExpenses)
            }.collect { (budget, totalSpent, todayExpenses) ->
                val todayTotal = todayExpenses.sumOf { it.amount }

                if (budget != null) {
                    val daysPassed = DateUtils.getDaysPassedInMonth()
                    val totalDays = DateUtils.getDaysInMonth(monthYear)

                    val prediction = PredictionEngine.calculateFullPrediction(
                        usableBudget = budget.usableBudget,
                        totalSpent = totalSpent,
                        daysPassed = daysPassed,
                        totalDaysInMonth = totalDays
                    )

                    // Build chart data: predicted end balance for each day passed
                    val chartData = buildChartData(budget, monthYear, daysPassed, totalDays)

                    _uiState.value = DashboardUiState(
                        budget = budget,
                        prediction = prediction,
                        todayExpenses = todayExpenses,
                        todayTotal = todayTotal,
                        chartData = chartData,
                        isLoading = false,
                        hasBudget = true
                    )
                } else {
                    _uiState.value = DashboardUiState(
                        todayExpenses = todayExpenses,
                        todayTotal = todayTotal,
                        isLoading = false,
                        hasBudget = false
                    )
                }
            }
        }
    }

    private suspend fun buildChartData(
        budget: Budget,
        monthYear: String,
        daysPassed: Int,
        totalDays: Int
    ): List<Double> {
        val chartData = mutableListOf<Double>()
        val firstDay = DateUtils.getFirstDayOfMonth(monthYear)

        for (day in 1..daysPassed) {
            val dateEnd = DateUtils.dateForDay(monthYear, day)
            val spentUpToDay = repository.getTotalSpentBetweenSync(firstDay, dateEnd)
            val prediction = PredictionEngine.calculateFullPrediction(
                usableBudget = budget.usableBudget,
                totalSpent = spentUpToDay,
                daysPassed = day,
                totalDaysInMonth = totalDays
            )
            chartData.add(prediction.predictedEndBalance)
        }

        return chartData
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlowlyApplication
                return DashboardViewModel(app.repository) as T
            }
        }
    }
}
