package com.lq.joy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lq.joy.data.AppContainer
import com.lq.joy.ui.page.detail.DetailScreen
import com.lq.joy.ui.page.detail.DetailViewModel
import com.lq.joy.ui.page.home.HomeScreen
import com.lq.joy.ui.page.search.SearchScreen

@Composable
fun JoyNavGraph(
    appContainer: AppContainer,
    isExpandedScreen: Boolean,
    navController: NavHostController,
    navigationActions: NavigationActions
) {
    NavHost(navController = navController, startDestination = Destinations.Home.route) {
        jump(Destinations.Home) {
            HomeScreen(
                appContainer,
                onSearchClick = { navigationActions.navigateToSearch() },
                onAnimationClick = { navigationActions.navigateToDetail(it.detailUrl) },
                onMoreClick = { navigationActions.navigationToMore(it) }
            )
        }

        jump(Destinations.Search) {
            SearchScreen()
        }

        jump(Destinations.Detail) { backStackEntry ->
            val viewModel: DetailViewModel =
                viewModel(
                    factory = DetailViewModel.providerFactory(
                        sakuraRepository = appContainer.sakuraRepository,
                        owner = backStackEntry,
                        defaultArgs = backStackEntry.arguments
                    )
                )
            DetailScreen(
                viewModel,
                isExpandedScreen,
                onRecommendClick = { navigationActions.navigateToDetail(it) })
        }

        jump(Destinations.More) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "更多页面")
            }
        }
    }
}

fun NavGraphBuilder.jump(
    destinations: Destinations,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(destinations.route, arguments, deepLinks, content)
}