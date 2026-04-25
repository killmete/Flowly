package com.example.flowly.ui.screens.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.flowly.FlowlyApplication
import com.example.flowly.data.local.entity.Expense
import com.example.flowly.data.repository.FlowlyRepository
import com.example.flowly.util.DateUtils
import com.example.flowly.util.ExpenseCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddExpenseUiState(
    val amount: String = "",
    val selectedCategory: ExpenseCategory = ExpenseCategory.FOOD,
    val note: String = "",
    val date: String = DateUtils.today(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class AddExpenseViewModel(private val repository: FlowlyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    fun updateAmount(value: String) {
        // Allow only valid decimal input
        val filtered = value.filter { it.isDigit() || it == '.' }
        if (filtered.count { it == '.' } <= 1) {
            _uiState.value = _uiState.value.copy(amount = filtered, error = null)
        }
    }

    fun updateCategory(category: ExpenseCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun updateNote(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun updateDate(value: String) {
        _uiState.value = _uiState.value.copy(date = value)
    }

    fun saveExpense() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _uiState.value = state.copy(error = "Please enter a valid amount")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)

            try {
                repository.insertExpense(
                    Expense(
                        amount = amount,
                        category = state.selectedCategory.name,
                        note = state.note.trim(),
                        date = state.date
                    )
                )
                _uiState.value = AddExpenseUiState(saveSuccess = true)
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isSaving = false,
                    error = "Failed to save expense"
                )
            }
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
                return AddExpenseViewModel(app.repository) as T
            }
        }
    }
}
