package com.example.flowly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val monthYear: String, // yyyy-MM format
    val monthlyIncome: Double = 0.0,
    val phoneBill: Double = 0.0,
    val internetBill: Double = 0.0,
    val subscriptions: Double = 0.0,
    val dailyNecessities: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalFixedBills: Double
        get() = phoneBill + internetBill + subscriptions + dailyNecessities

    val usableBudget: Double
        get() = monthlyIncome - totalFixedBills
}
