@file:OptIn(ExperimentalAnimationApi::class)

package com.lq.joy

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.lq.joy.data.AppContainer
import com.lq.joy.ui.page.common.JumpAnimationType
import com.lq.joy.ui.page.common.PageAnimation
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
    isExpandedScreen: Boolean,
    navController: NavHostController,
    navigationActions: NavigationActions
) {
    AnimatedNavHost(navController = navController, startDestination = Destinations.Main.route) {
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


            NaifeiDetailScreen(
                viewModel = viewModel,
                isExpandedScreen = isExpandedScreen,
                onRecommendClick = {},
                onPageFinish = { navController.popBackStack() })

        }
    }
}
private

fun NavGraphBuilder.jump(
    destinations: Destinations,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    jumpAnimation: JumpAnimationType = JumpAnimationType.DEFAULT,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    val animation = PageAnimation(jumpAnimation)

    composable(
        route = destinations.route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = {
            animation.enterTransition
        },
        exitTransition = {
            animation.exitTransition
        },
        popEnterTransition = {
            animation.popEnterTransition
        },
        popExitTransition = {
            animation.popExitTransition
        },
        content = { content(it) }
    )
}