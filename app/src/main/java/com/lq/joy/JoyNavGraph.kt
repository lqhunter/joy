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
import androidx.navigation.navArgument
import com.lq.joy.JoyDestinations.DETAIL_PARAMS_ID
import com.lq.joy.data.AppContainer
import com.lq.joy.ui.page.detail.DetailScreen
import com.lq.joy.ui.page.home.HomeScreen

@Composable
fun JoyNavGraph(
    appContainer: AppContainer,
    navController: NavHostController,
    navigationActions: NavigationActions
) {
    NavHost(navController = navController, startDestination = JoyDestinations.HOME) {
        composable(JoyDestinations.HOME) {
            HomeScreen(
                appContainer,
                onSearchClick = { navigationActions.navigateToSearch() },
                onAnimationClick = { navigationActions.navigateToDetail(it.id) },
                onMoreClick = { navigationActions.navigationToMore(it)}
            )
        }

        composable(JoyDestinations.SEARCH) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "搜索页面")
            }
        }

        composable(JoyDestinations.DETAIL_WITH_PARAMS, arguments = listOf(navArgument(DETAIL_PARAMS_ID) {})) {
            it.arguments?.getString(DETAIL_PARAMS_ID)?.let { url ->
                DetailScreen(appContainer, url)
            }
        }

        composable(JoyDestinations.MORE) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "更多页面")
            }
        }
    }
}