package com.lq.joy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.systemuicontroller.SystemUiController
import com.lq.joy.data.AppContainer
import com.lq.joy.ui.page.detail.naifei.NaifeiDetailScreen
import com.lq.joy.ui.page.detail.naifei.NaifeiDetailViewModel
import com.lq.joy.ui.page.detail.sakura.SakuraDetailScreen
import com.lq.joy.ui.page.detail.sakura.SakuraDetailViewModel
import com.lq.joy.ui.page.home.HomeScreen
import com.lq.joy.ui.page.mian.MainScreen
import com.lq.joy.ui.page.search.SearchScreen
import com.lq.joy.ui.page.search.SearchViewModel

@Composable
fun JoyNavGraph(
    appContainer: AppContainer,
    isLandscape: Boolean,
    navController: NavHostController,
    systemUiController: SystemUiController,
    navigationActions: NavigationActions
) {
    NavHost(navController = navController, startDestination = Destinations.Main.route) {
        jump(Destinations.Home) {
            HomeScreen(
                appContainer,
                onSearchClick = { navigationActions.navigateToSearch() },
                onAnimationClick = { },
                onMoreClick = { navigationActions.navigationToMore(it) }
            )
        }

        jump(Destinations.Main) {
            MainScreen(appContainer, onSearchClick = { navigationActions.navigateToSearch() })
        }

        jump(Destinations.Search) {
            val viewModel: SearchViewModel =
                viewModel(
                    factory = SearchViewModel.providerFactory(
                        appContainer.sakuraRepository,
                        appContainer.naifeiRepository
                    )
                )
            SearchScreen(viewModel, onNaifeiSelected = {
                navigationActions.navigateToNaifeiDetail(it)
            }, onSakuraSelected = { navigationActions.navigateToSakuraDetail(it) })
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
                isLandscape,
                onRecommendClick = { navigationActions.navigateToSakuraDetail(it) },
                systemUiController = systemUiController
            )
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

            val originalOrientation = LocalContext.current.findActivity()!!.requestedOrientation
            NaifeiDetailScreen(
                viewModel = viewModel,
                isExpandedScreen = isLandscape,
                onRecommendClick = { navigationActions.navigateToNaifeiDetail(it) },
                systemUiController = systemUiController,
                finish = { navController.popBackStack() },
                originalOrientation = originalOrientation
            )

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