package com.example.flowly.data.repository

import com.example.flowly.data.local.dao.BudgetDao
import com.example.flowly.data.local.dao.ExpenseDao
import com.example.flowly.data.local.entity.Budget
import com.example.flowly.data.local.entity.Expense
import kotlinx.coroutines.flow.Flow

class FlowlyRepository(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao
) {
    // ── Expense operations ──────────────────────────────────────────

    suspend fun insertExpense(expense: Expense): Long = expenseDao.insert(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.update(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.delete(expense)

    suspend fun deleteExpenseById(id: Long) = expenseDao.deleteById(id)

    fun getExpensesByDate(date: String): Flow<List<Expense>> =
        expenseDao.getExpensesByDate(date)

    fun getExpensesBetween(start: String, end: String): Flow<List<Expense>> =
        expenseDao.getExpensesBetween(start, end)

    fun getTotalSpentBetween(start: String, end: String): Flow<Double> =
        expenseDao.getTotalSpentBetween(start, end)

    fun getTotalSpentOnDate(date: String): Flow<Double> =
        expenseDao.getTotalSpentOnDate(date)

    suspend fun getExpensesBetweenSync(start: String, end: String): List<Expense> =
        expenseDao.getExpensesBetweenSync(start, end)

    suspend fun getTotalSpentBetweenSync(start: String, end: String): Double =
        expenseDao.getTotalSpentBetweenSync(start, end)

    // ── Budget operations ───────────────────────────────────────────

    suspend fun insertBudget(budget: Budget): Long = budgetDao.insert(budget)

    suspend fun updateBudget(budget: Budget) = budgetDao.update(budget)

    fun getBudgetForMonth(monthYear: String): Flow<Budget?> =
        budgetDao.getBudgetForMonth(monthYear)

    suspend fun getBudgetForMonthSync(monthYear: String): Budget? =
        budgetDao.getBudgetForMonthSync(monthYear)

    fun getAllBudgets(): Flow<List<Budget>> =
        budgetDao.getAllBudgets()
}
