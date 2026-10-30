package com.wzbx.cinetrack.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wzbx.cinetrack.ui.screens.DiscoverScreen
import com.wzbx.cinetrack.ui.screens.ReportScreen
import com.wzbx.cinetrack.ui.screens.WatchlistScreen

private data class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val tabs = listOf(
    BottomTab("discover", "发现", Icons.Filled.Home),
    BottomTab("watchlist", "看单", Icons.Filled.Star),
    BottomTab("report", "报告", Icons.Filled.DateRange)
)

/**
 * App 骨架：底部导航 + 三个页面。
 *
 * 导航写法说明：
 * - `launchSingleTop` 防止连点同一个 tab 压出一堆重复页面；
 * - `saveState / restoreState` 让切回来时滚动位置还在；
 * - `popUpTo(startDestinationId)` 保证返回栈不会越切越深。
 */
@Composable
fun CineTrackApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            if (currentRoute != tab.route) {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "discover",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("discover") { DiscoverScreen() }
            composable("watchlist") {
                WatchlistScreen(onGoDiscover = { navController.navigate("discover") })
            }
            composable("report") { ReportScreen() }
        }
    }
}
