package com.example.flowly.util

enum class SpendingStatus {
    SAFE, WARNING, DANGER
}

data class PredictionResult(
    val totalSpent: Double,
    val remaining: Double,
    val daysLeft: Int,
    val averageDailySpend: Double,
    val predictedMonthlySpend: Double,
    val predictedEndBalance: Double,
    val safeDailyBudget: Double,
    val status: SpendingStatus
)

object PredictionEngine {

    fun calculateAverageDailySpend(
        totalSpent: Double,
        daysPassed: Int
    ): Double {
        if (daysPassed <= 0) return 0.0
        return totalSpent / daysPassed
    }

    fun predictMonthlySpend(
        averageDailySpend: Double,
        totalDaysInMonth: Int
    ): Double {
        return averageDailySpend * totalDaysInMonth
    }

    fun predictEndBalance(
        usableBudget: Double,
        predictedMonthlySpend: Double
    ): Double {
        return usableBudget - predictedMonthlySpend
    }

    fun calculateSafeDailyBudget(
        remainingBudget: Double,
        daysLeft: Int
    ): Double {
        if (daysLeft <= 0) return 0.0
        return (remainingBudget / daysLeft).coerceAtLeast(0.0)
    }

    fun getSpendingStatus(
        predictedEndBalance: Double,
        usableBudget: Double
    ): SpendingStatus {
        if (usableBudget <= 0) return SpendingStatus.DANGER
        val ratio = predictedEndBalance / usableBudget
        return when {
            ratio >= 0.2 -> SpendingStatus.SAFE
            ratio >= 0.0 -> SpendingStatus.WARNING
            else -> SpendingStatus.DANGER
        }
    }

    /**
     * Run the full prediction pipeline for a given budget state.
     *
     * @param usableBudget  monthly income minus fixed bills
     * @param totalSpent    total spending from day 1 up to today
     * @param daysPassed    how many days of the month have elapsed (including today)
     * @param totalDaysInMonth  total calendar days in the current month
     */
    fun calculateFullPrediction(
        usableBudget: Double,
        totalSpent: Double,
        daysPassed: Int,
        totalDaysInMonth: Int
    ): PredictionResult {
        val daysLeft = totalDaysInMonth - daysPassed
        val remaining = usableBudget - totalSpent
        val avgDaily = calculateAverageDailySpend(totalSpent, daysPassed)
        val predictedSpend = predictMonthlySpend(avgDaily, totalDaysInMonth)
        val predictedEnd = predictEndBalance(usableBudget, predictedSpend)
        val safeDailyBudget = calculateSafeDailyBudget(remaining, daysLeft)
        val status = getSpendingStatus(predictedEnd, usableBudget)

        return PredictionResult(
            totalSpent = totalSpent,
            remaining = remaining,
            daysLeft = daysLeft,
            averageDailySpend = avgDaily,
            predictedMonthlySpend = predictedSpend,
            predictedEndBalance = predictedEnd,
            safeDailyBudget = safeDailyBudget,
            status = status
        )
    }
}
