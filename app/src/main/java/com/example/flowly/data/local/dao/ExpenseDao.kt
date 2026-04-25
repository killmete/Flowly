package com.example.flowly.data.local.dao

import androidx.room.*
import com.example.flowly.data.local.entity.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY createdAt DESC")
    fun getExpensesByDate(date: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, createdAt DESC")
    fun getExpensesBetween(startDate: String, endDate: String): Flow<List<Expense>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalSpentBetween(startDate: String, endDate: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE date = :date")
    fun getTotalSpentOnDate(date: String): Flow<Double>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getExpensesBetweenSync(startDate: String, endDate: String): List<Expense>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalSpentBetweenSync(startDate: String, endDate: String): Double
}
