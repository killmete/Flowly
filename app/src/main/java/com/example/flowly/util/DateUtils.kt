package com.example.flowly.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val dateFormat get() = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val monthYearFormat get() = SimpleDateFormat("yyyy-MM", Locale.US)
    private val displayDateFormat get() = SimpleDateFormat("MMM dd", Locale.US)
    private val displayMonthFormat get() = SimpleDateFormat("MMMM yyyy", Locale.US)
    private val dayOfWeekFormat get() = SimpleDateFormat("EEE", Locale.US)
    private val dayNumberFormat get() = SimpleDateFormat("dd", Locale.US)

    fun today(): String = dateFormat.format(Date())

    fun currentMonthYear(): String = monthYearFormat.format(Date())

    fun getDaysInMonth(monthYear: String): Int {
        val cal = Calendar.getInstance()
        val parts = monthYear.split("-")
        cal.set(Calendar.YEAR, parts[0].toInt())
        cal.set(Calendar.MONTH, parts[1].toInt() - 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getDayOfMonth(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }

    fun getDayOfMonth(date: String): Int {
        val parts = date.split("-")
        return parts[2].toInt()
    }

    fun getFirstDayOfMonth(monthYear: String): String = "$monthYear-01"

    fun getLastDayOfMonth(monthYear: String): String {
        val days = getDaysInMonth(monthYear)
        return "$monthYear-${String.format(Locale.US, "%02d", days)}"
    }

    fun formatDateForDisplay(date: String): String {
        return try {
            val parsed = dateFormat.parse(date)
            if (parsed != null) displayDateFormat.format(parsed) else date
        } catch (e: Exception) {
            date
        }
    }

    fun formatMonthYearForDisplay(monthYear: String): String {
        return try {
            val parsed = monthYearFormat.parse(monthYear)
            if (parsed != null) displayMonthFormat.format(parsed) else monthYear
        } catch (e: Exception) {
            monthYear
        }
    }

    fun getDayOfWeek(date: String): String {
        return try {
            val parsed = dateFormat.parse(date)
            if (parsed != null) dayOfWeekFormat.format(parsed) else ""
        } catch (e: Exception) {
            ""
        }
    }

    fun dateForDay(monthYear: String, day: Int): String {
        return "$monthYear-${String.format(Locale.US, "%02d", day)}"
    }

    fun getDaysPassedInMonth(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }

    fun getDaysLeftInMonth(): Int {
        val cal = Calendar.getInstance()
        val totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        return totalDays - currentDay
    }

    fun getDaysInCurrentMonth(): Int {
        return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}
