package com.lq.joy

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lq.joy.data.AppContainer
import com.lq.joy.ui.page.detail.naifei.NaifeiDetailScreen
import com.lq.joy.ui.page.detail.naifei.NaifeiDetailViewModel
import com.lq.joy.ui.page.detail.sakura.SakuraDetailScreen
import com.lq.joy.ui.page.detail.sakura.SakuraDetailViewModel
import com.lq.joy.ui.page.home.HomeScreen
import com.lq.joy.ui.page.search.SearchScreen
import com.lq.joy.ui.page.search.SearchViewModel

@Composable
fun JoyNavGraph(
    appContainer: AppContainer,
    isExpandedScreen: Boolean,
    navController: NavHostController,
    navigationActions: NavigationActions
) {
    NavHost(navController = navController, startDestination = Destinations.Search.route) {
        jump(Destinations.Home) {
            HomeScreen(
                appContainer,
                onSearchClick = { navigationActions.navigateToSearch() },
                onAnimationClick = { },
                onMoreClick = { navigationActions.navigationToMore(it) }
            )
        }

        jump(Destinations.Search) {
            val viewModel: SearchViewModel =
                viewModel(
                    factory = SearchViewModel.providerFactory(
                        appContainer.sakuraRepository,
                        appContainer.naifeiRepository
                    )
                )
            SearchScreen(viewModel) {
                navigationActions.navigateToNaifeiDetail(it)
            }
        }

        jump(Destinations.SakuraDetail) { backStackEntry ->
            val viewModel: SakuraDetailViewModel =
                viewModel(
                    factory = SakuraDetailViewModel.providerFactory(
                        sakuraRepository = appContainer.sakuraRepository,
                        owner = backStackEntry,
                        defaultArgs = backStackEntry.arguments
                    )
                )
            SakuraDetailScreen(
                viewModel,
                isExpandedScreen,
                onRecommendClick = { navigationActions.navigateToSakuraDetail(it) })
        }

        jump(Destinations.More) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "更多页面")
            }
        }

        jump(Destinations.NaifeiDetail) { backStackEntry ->
            val viewModel: NaifeiDetailViewModel =
                viewModel(
                    factory = NaifeiDetailViewModel.providerFactory(
                        naifeiRepository = appContainer.naifeiRepository,
                        owner = backStackEntry,
                        defaultArgs = backStackEntry.arguments
                    )
                )


            NaifeiDetailScreen(viewModel = viewModel, isExpandedScreen = isExpandedScreen, onRecommendClick = {})

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