package com.example.flowly.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flowly.ui.screens.addexpense.AddExpenseScreen
import com.example.flowly.ui.screens.addexpense.AddExpenseViewModel
import com.example.flowly.ui.screens.dashboard.DashboardScreen
import com.example.flowly.ui.screens.dashboard.DashboardViewModel
import com.example.flowly.ui.screens.settings.SettingsScreen
import com.example.flowly.ui.screens.settings.SettingsViewModel
import com.example.flowly.ui.screens.timeline.TimelineScreen
import com.example.flowly.ui.screens.timeline.TimelineViewModel
import com.example.flowly.ui.theme.DarkBackground
import com.example.flowly.ui.theme.DarkSurface
import com.example.flowly.ui.theme.PrimaryPurple
import com.example.flowly.ui.theme.TextMuted
import com.example.flowly.ui.theme.TextSecondary

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Dashboard : Screen("dashboard", "Home", Icons.Rounded.Dashboard, Icons.Outlined.Dashboard)
    data object AddExpense : Screen("add_expense", "Add", Icons.Rounded.AddCircle, Icons.Outlined.AddCircleOutline)
    data object Timeline : Screen("timeline", "Timeline", Icons.Rounded.Timeline, Icons.Outlined.Timeline)
    data object Settings : Screen("settings", "Settings", Icons.Rounded.Settings, Icons.Outlined.Settings)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.AddExpense,
    Screen.Timeline,
    Screen.Settings
)

@Composable
fun FlowlyNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            if (screen is Screen.AddExpense) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (selected) PrimaryPurple
                                            else PrimaryPurple.copy(alpha = 0.2f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = screen.title,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = {
                            if (screen !is Screen.AddExpense) {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryPurple,
                            unselectedIconColor = TextMuted,
                            selectedTextColor = PrimaryPurple,
                            unselectedTextColor = TextMuted,
                            indicatorColor = PrimaryPurple.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                }
            ) {
                composable(Screen.Dashboard.route) {
                    val vm: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
                    DashboardScreen(
                        viewModel = vm,
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Screen.AddExpense.route) {
                    val vm: AddExpenseViewModel = viewModel(factory = AddExpenseViewModel.Factory)
                    AddExpenseScreen(viewModel = vm)
                }
                composable(Screen.Timeline.route) {
                    val vm: TimelineViewModel = viewModel(factory = TimelineViewModel.Factory)
                    TimelineScreen(viewModel = vm)
                }
                composable(Screen.Settings.route) {
                    val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
                    SettingsScreen(viewModel = vm)
                }
            }
        }
    }
}
