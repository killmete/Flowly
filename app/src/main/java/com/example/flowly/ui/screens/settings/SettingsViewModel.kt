package com.example.flowly.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.flowly.FlowlyApplication
import com.example.flowly.data.local.entity.Budget
import com.example.flowly.data.repository.FlowlyRepository
import com.example.flowly.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val monthlyIncome: String = "",
    val phoneBill: String = "",
    val internetBill: String = "",
    val subscriptions: String = "",
    val dailyNecessities: String = "",
    val usableBudget: Double = 0.0,
    val totalFixed: Double = 0.0,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val existingBudgetId: Long = 0,
    val isLoading: Boolean = true
)

class SettingsViewModel(private val repository: FlowlyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentBudget()
    }

    private fun loadCurrentBudget() {
        val monthYear = DateUtils.currentMonthYear()
        viewModelScope.launch {
            repository.getBudgetForMonth(monthYear).collect { budget ->
                if (budget != null) {
                    _uiState.value = SettingsUiState(
                        monthlyIncome = if (budget.monthlyIncome > 0) budget.monthlyIncome.toString() else "",
                        phoneBill = if (budget.phoneBill > 0) budget.phoneBill.toString() else "",
                        internetBill = if (budget.internetBill > 0) budget.internetBill.toString() else "",
                        subscriptions = if (budget.subscriptions > 0) budget.subscriptions.toString() else "",
                        dailyNecessities = if (budget.dailyNecessities > 0) budget.dailyNecessities.toString() else "",
                        usableBudget = budget.usableBudget,
                        totalFixed = budget.totalFixedBills,
                        existingBudgetId = budget.id,
                        isLoading = false
                    )
                } else {
                    _uiState.value = SettingsUiState(isLoading = false)
                }
            }
        }
    }

    fun updateMonthlyIncome(value: String) {
        val filtered = filterNumericInput(value)
        _uiState.value = _uiState.value.copy(monthlyIncome = filtered)
        recalculate()
    }

    fun updatePhoneBill(value: String) {
        _uiState.value = _uiState.value.copy(phoneBill = filterNumericInput(value))
        recalculate()
    }

    fun updateInternetBill(value: String) {
        _uiState.value = _uiState.value.copy(internetBill = filterNumericInput(value))
        recalculate()
    }

    fun updateSubscriptions(value: String) {
        _uiState.value = _uiState.value.copy(subscriptions = filterNumericInput(value))
        recalculate()
    }

    fun updateDailyNecessities(value: String) {
        _uiState.value = _uiState.value.copy(dailyNecessities = filterNumericInput(value))
        recalculate()
    }

    private fun filterNumericInput(value: String): String {
        val filtered = value.filter { it.isDigit() || it == '.' }
        // If there are multiple dots, keep only up to the first dot section
        val dotIndex = filtered.indexOf('.')
        return if (dotIndex >= 0) {
            val beforeDot = filtered.substring(0, dotIndex)
            val afterDot = filtered.substring(dotIndex + 1).replace(".", "")
            "$beforeDot.$afterDot"
        } else {
            filtered
        }
    }

    private fun recalculate() {
        val state = _uiState.value
        val income = state.monthlyIncome.toDoubleOrNull() ?: 0.0
        val phone = state.phoneBill.toDoubleOrNull() ?: 0.0
        val internet = state.internetBill.toDoubleOrNull() ?: 0.0
        val subs = state.subscriptions.toDoubleOrNull() ?: 0.0
        val necessities = state.dailyNecessities.toDoubleOrNull() ?: 0.0

        val totalFixed = phone + internet + subs + necessities
        val usable = income - totalFixed

        _uiState.value = state.copy(
            usableBudget = usable,
            totalFixed = totalFixed
        )
    }

    fun saveBudget() {
        val state = _uiState.value
        val income = state.monthlyIncome.toDoubleOrNull() ?: 0.0

        if (income <= 0) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)

            val budget = Budget(
                id = if (state.existingBudgetId > 0) state.existingBudgetId else 0,
                monthYear = DateUtils.currentMonthYear(),
                monthlyIncome = income,
                phoneBill = state.phoneBill.toDoubleOrNull() ?: 0.0,
                internetBill = state.internetBill.toDoubleOrNull() ?: 0.0,
                subscriptions = state.subscriptions.toDoubleOrNull() ?: 0.0,
                dailyNecessities = state.dailyNecessities.toDoubleOrNull() ?: 0.0
            )

            repository.insertBudget(budget)

            _uiState.value = _uiState.value.copy(
                isSaving = false,
                saveSuccess = true
            )
        }
    }

    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlowlyApplication
                return SettingsViewModel(app.repository) as T
            }
        }
    }
}
