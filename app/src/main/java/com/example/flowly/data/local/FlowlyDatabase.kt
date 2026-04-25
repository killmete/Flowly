package com.example.flowly.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flowly.data.local.dao.BudgetDao
import com.example.flowly.data.local.dao.ExpenseDao
import com.example.flowly.data.local.entity.Budget
import com.example.flowly.data.local.entity.Expense

@Database(
    entities = [Expense::class, Budget::class],
    version = 1,
    exportSchema = false
)
abstract class FlowlyDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: FlowlyDatabase? = null

        fun getDatabase(context: Context): FlowlyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlowlyDatabase::class.java,
                    "flowly_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
