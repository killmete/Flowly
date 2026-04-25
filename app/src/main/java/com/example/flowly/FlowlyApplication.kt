package com.example.flowly

import android.app.Application
import com.example.flowly.data.local.FlowlyDatabase
import com.example.flowly.data.repository.FlowlyRepository

class FlowlyApplication : Application() {

    val database by lazy { FlowlyDatabase.getDatabase(this) }

    val repository by lazy {
        FlowlyRepository(database.expenseDao(), database.budgetDao())
    }
}
