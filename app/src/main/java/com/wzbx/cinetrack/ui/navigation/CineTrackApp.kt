package com.wzbx.cinetrack.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wzbx.cinetrack.ui.detail.DetailScreen
import com.wzbx.cinetrack.ui.screens.DiscoverScreen
import com.wzbx.cinetrack.ui.screens.ReportScreen
import com.wzbx.cinetrack.ui.screens.SearchScreen
import com.wzbx.cinetrack.ui.screens.WatchlistScreen

private data class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val tabs = listOf(
    BottomTab("discover", "发现", Icons.Filled.Home),
    BottomTab("search", "搜索", Icons.Filled.Search),
    BottomTab("watchlist", "看单", Icons.Filled.Star),
    BottomTab("report", "报告", Icons.Filled.DateRange)
)

/**
 * App 骨架：底部四个 tab + 详情页。
 *
 * 详情页不在底部导航里，但同属一个 NavHost，
 * 返回栈由 Navigation 统一管理，从搜索页和发现页都能进去。
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
            composable("discover") {
                DiscoverScreen(onMovieClick = { movieId ->
                    navController.navigate("detail/$movieId")
                })
            }

            composable("search") {
                SearchScreen(onMovieClick = { movieId ->
                    navController.navigate("detail/$movieId")
                })
            }

            composable("watchlist") {
                WatchlistScreen(onGoDiscover = { navController.navigate("discover") })
            }

            composable("report") { ReportScreen() }

            composable(
                route = "detail/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.LongType })
            ) { entry ->
                DetailScreen(
                    movieId = entry.arguments?.getLong("movieId") ?: 0L,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
