package com.example.flowly.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Router
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.flowly.ui.components.AnimatedCounter
import com.example.flowly.ui.theme.DangerRed
import com.example.flowly.ui.theme.DarkCard
import com.example.flowly.ui.theme.PrimaryPurple
import com.example.flowly.ui.theme.SafeGreen
import com.example.flowly.ui.theme.TextMuted
import com.example.flowly.ui.theme.TextSecondary
import com.example.flowly.util.DateUtils
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val state by viewModel.uiState.collectAsState()

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            delay(2000)
            viewModel.resetSaveSuccess()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { -40 }
            ) {
                Column {
                    Text(
                        text = DateUtils.formatMonthYearForDisplay(DateUtils.currentMonthYear()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Budget Setup",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Monthly Income
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { 60 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(DarkCard)
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AttachMoney,
                            contentDescription = null,
                            tint = SafeGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Monthly Income / Allowance",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.monthlyIncome,
                        onValueChange = { viewModel.updateMonthlyIncome(it) },
                        label = { Text("Amount") },
                        prefix = { Text("$ ", color = SafeGreen) },
                        placeholder = { Text("e.g. 3000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SafeGreen,
                            unfocusedBorderColor = TextMuted,
                            focusedLabelColor = SafeGreen,
                            unfocusedLabelColor = TextSecondary,
                            cursorColor = SafeGreen
                        )
                    )
                }
            }
        }

        // Fixed Bills
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { 80 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(DarkCard)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Fixed Monthly Bills",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Expected expenses that repeat every month",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    HorizontalDivider(color = TextMuted.copy(alpha = 0.3f))

                    BillField(
                        label = "Phone Bill",
                        value = state.phoneBill,
                        onValueChange = { viewModel.updatePhoneBill(it) },
                        icon = Icons.Rounded.PhoneAndroid
                    )
                    BillField(
                        label = "Internet",
                        value = state.internetBill,
                        onValueChange = { viewModel.updateInternetBill(it) },
                        icon = Icons.Rounded.Router
                    )
                    BillField(
                        label = "Subscriptions",
                        value = state.subscriptions,
                        onValueChange = { viewModel.updateSubscriptions(it) },
                        icon = Icons.Rounded.Subscriptions
                    )
                    BillField(
                        label = "Daily Necessities (est.)",
                        value = state.dailyNecessities,
                        onValueChange = { viewModel.updateDailyNecessities(it) },
                        icon = Icons.Rounded.ShoppingCart
                    )
                }
            }
        }

        // Usable Budget Summary
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { 100 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            if (state.usableBudget >= 0) SafeGreen.copy(alpha = 0.08f)
                            else DangerRed.copy(alpha = 0.08f)
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Usable Budget",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AnimatedCounter(
                        value = state.usableBudget,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (state.usableBudget >= 0) SafeGreen else DangerRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Income ($${"%.2f".format(state.monthlyIncome.toDoubleOrNull() ?: 0.0)}) − Fixed Bills ($${"%.2f".format(state.totalFixed)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Save Button
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { 120 }
            ) {
                Button(
                    onClick = { viewModel.saveBudget() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !state.isSaving && (state.monthlyIncome.toDoubleOrNull() ?: 0.0) > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple
                    )
                ) {
                    Text(
                        text = when {
                            state.saveSuccess -> "✓ Saved!"
                            state.isSaving -> "Saving..."
                            state.existingBudgetId > 0 -> "Update Budget"
                            else -> "Save Budget"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        // Bottom spacing
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun BillField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        prefix = { Text("$ ") },
        placeholder = { Text("0.00") },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PrimaryPurple,
                modifier = Modifier.size(20.dp)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryPurple,
            unfocusedBorderColor = TextMuted,
            focusedLabelColor = PrimaryPurple,
            unfocusedLabelColor = TextSecondary,
            cursorColor = PrimaryPurple
        )
    )
}
