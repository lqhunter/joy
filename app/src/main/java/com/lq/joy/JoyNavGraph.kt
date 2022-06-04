package com.lq.joy

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lq.joy.data.AppContainer
import com.lq.joy.ui.page.detail.DetailScreen
import com.lq.joy.ui.page.detail.DetailViewModel
import com.lq.joy.ui.page.home.HomeScreen

@Composable
fun JoyNavGraph(
    appContainer: AppContainer,
    isExpandedScreen:Boolean,
    navController: NavHostController,
    navigationActions: NavigationActions
) {
    NavHost(navController = navController, startDestination = Destinations.Home.route) {
        composable(Destinations.Home.route) {
            HomeScreen(
                appContainer,
                onSearchClick = { navigationActions.navigateToSearch() },
                onAnimationClick = { navigationActions.navigateToDetail(it.detailUrl) },
                onMoreClick = { navigationActions.navigationToMore(it) }
            )
        }

        composable(Destinations.Search.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "搜索页面")
            }
        }

        composable(Destinations.Detail.route) { backStackEntry ->
            val viewModel: DetailViewModel =
                viewModel(
                    factory = DetailViewModel.providerFactory(
                        sakuraRepository = appContainer.sakuraRepository,
                        owner = backStackEntry,
                        defaultArgs = backStackEntry.arguments
                    )
                )
            DetailScreen(viewModel, isExpandedScreen, onRecommendClick = { navigationActions.navigateToDetail(it) })
        }

        composable(Destinations.More.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "更多页面")
            }
        }
    }
}