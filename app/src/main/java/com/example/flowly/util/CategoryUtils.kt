package com.example.flowly.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.flowly.ui.theme.CategoryBills
import com.example.flowly.ui.theme.CategoryEntertainment
import com.example.flowly.ui.theme.CategoryFood
import com.example.flowly.ui.theme.CategoryOther
import com.example.flowly.ui.theme.CategoryShopping
import com.example.flowly.ui.theme.CategoryTransport

enum class ExpenseCategory(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    FOOD("Food", Icons.Rounded.Fastfood, CategoryFood),
    TRANSPORT("Transport", Icons.Rounded.DirectionsCar, CategoryTransport),
    SHOPPING("Shopping", Icons.Rounded.ShoppingBag, CategoryShopping),
    ENTERTAINMENT("Entertainment", Icons.Rounded.Movie, CategoryEntertainment),
    BILLS("Bills", Icons.Rounded.Receipt, CategoryBills),
    OTHER("Other", Icons.Rounded.MoreHoriz, CategoryOther);

    companion object {
        fun fromString(value: String): ExpenseCategory {
            return try {
                valueOf(value.uppercase())
            } catch (e: Exception) {
                OTHER
            }
        }
    }
}
