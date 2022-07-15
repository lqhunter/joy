package com.lq.joy

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.systemuicontroller.SystemUiController
import com.lq.joy.data.AppContainer
import com.lq.joy.data.SourceType
import com.lq.joy.data.ui.RecommendBean
import com.lq.joy.ui.page.detail.DetailScreen
import com.lq.joy.ui.page.detail.NaifeiDetailViewModel
import com.lq.joy.ui.page.detail.SakuraDetailViewModel
import com.lq.joy.ui.page.favourite.FavouriteScreen
import com.lq.joy.ui.page.favourite.FavouriteViewModel
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

    //每次进入搜索页，键盘都会弹出来，这里控制只弹出一次。这样写感觉不太好
    var searchScreenKeyBoard by remember {
        mutableStateOf(true)
    }

    NavHost(navController = navController, startDestination = Destinations.Main.route) {
        jump(Destinations.Main) {
            MainScreen(
                appContainer,
                onSearchClick = {
                    searchScreenKeyBoard = true
                    navigationActions.navigateToSearch()
                },
                onFavouriteClick = {
                    Log.d(TAG, "navigateToFavourite")
                    navigationActions.navigateToFavourite()
                })
        }

        jump(Destinations.Search) {
            val viewModel: SearchViewModel =
                viewModel(
                    factory = SearchViewModel.providerFactory(
                        appContainer.sakuraRepository,
                        appContainer.naifeiRepository,
                        appContainer.appRepository
                    )
                )
            SearchScreen(
                viewModel,
                onNaifeiSelected = {
                    navigationActions.navigateToNaifeiDetail(it)
                },
                onSakuraSelected = { navigationActions.navigateToSakuraDetail(it) },
                appRepository = appContainer.appRepository,
                searchScreenKeyBoard = searchScreenKeyBoard
            )
        }

        jump(Destinations.Favourite) {
            Log.d(TAG, "jumpToFavourite")

            val viewModel: FavouriteViewModel = viewModel(
                factory = FavouriteViewModel.providerFactory(
                    appContainer.appRepository
                )
            )
            FavouriteScreen(viewModel, finish = {
                navController.popBackStack()
            }, jumpDetail = { sourceType, jumpKey ->
                when (sourceType) {
                    SourceType.SAKURA.ordinal -> {
                        navigationActions.navigateToSakuraDetail(jumpKey)
                    }
                    SourceType.NAIFEI.ordinal -> {
                        navigationActions.navigateToNaifeiDetail(jumpKey.toInt())
                    }
                }

            })
        }

        jump(Destinations.SakuraDetail) { backStackEntry ->
            val viewModel: SakuraDetailViewModel =
                viewModel(
                    factory = SakuraDetailViewModel.providerFactory(
                        sakuraRepository = appContainer.sakuraRepository,
                        appRepository = appContainer.appRepository,
                        owner = backStackEntry,
                        defaultArgs = backStackEntry.arguments
                    )
                )

            val originalOrientation = LocalContext.current.findActivity()!!.requestedOrientation
            DetailScreen(
                detailType = SourceType.SAKURA,
                viewModel = viewModel,
                isExpandedScreen = isLandscape,
                onRecommendClick = {
                    if (it is RecommendBean.NaifeiRecommend) {
                        navigationActions.navigateToNaifeiDetail(it.id)
                    } else if (it is RecommendBean.SakuraRecommend) {
                        navigationActions.navigateToSakuraDetail(it.htmlUrl)
                    }
                },
                systemUiController = systemUiController,
                finish = {
                    searchScreenKeyBoard = false
                    navController.popBackStack()
                },
                originalOrientation = originalOrientation
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
            DetailScreen(
                detailType = SourceType.NAIFEI,
                viewModel = viewModel,
                isExpandedScreen = isLandscape,
                onRecommendClick = {
                    if (it is RecommendBean.NaifeiRecommend) {
                        navigationActions.navigateToNaifeiDetail(it.id)
                    } else if (it is RecommendBean.SakuraRecommend) {
                        navigationActions.navigateToSakuraDetail(it.htmlUrl)
                    }
                },
                systemUiController = systemUiController,
                finish = {
                    searchScreenKeyBoard = false
                    navController.popBackStack()
                },
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