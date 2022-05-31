package com.lq.joy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lq.joy.data.AppContainer
import com.lq.joy.ui.home.HomeScreen

@Composable
fun JoyNavGraph(
    appContainer: AppContainer,
    navController: NavHostController,
    navigationActions: NavigationActions
) {
    NavHost(navController = navController, startDestination = JoyDestinations.HOME) {
        composable(JoyDestinations.HOME) {
            HomeScreen(appContainer, onSearchClick = { navigationActions.navigateToSearch() })
        }

        composable(JoyDestinations.SEARCH) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "搜索页面")
            }
        }
    }
}