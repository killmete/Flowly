package com.example.flowly.data.local.dao

import androidx.room.*
import com.example.flowly.data.local.entity.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget)

    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear LIMIT 1")
    fun getBudgetForMonth(monthYear: String): Flow<Budget?>

    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear LIMIT 1")
    suspend fun getBudgetForMonthSync(monthYear: String): Budget?

    @Query("SELECT * FROM budgets ORDER BY monthYear DESC")
    fun getAllBudgets(): Flow<List<Budget>>
}
