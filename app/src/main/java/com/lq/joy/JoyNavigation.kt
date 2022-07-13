package com.lq.joy

import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.navOptions
import com.lq.joy.data.ui.VideoSearchBean

sealed class Destinations(val route: String) {
    object Main : Destinations("main")
    object Favourite : Destinations("favourite")

    object Search : Destinations("search")
    object More : Destinations("more")

    object SakuraDetail : Destinations("sakuraDetail/{episodeUri}") {
        fun createRoute(episodeUri: String) = "sakuraDetail/$episodeUri"
    }

    object NaifeiDetail : Destinations("naifeiDetail")

}


class NavigationActions(navController: NavController) {

    val navigateToMain: () -> Unit = {
        navController.navigate(Destinations.Main.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToSearch: () -> Unit = {
        navController.navigate(Destinations.Search.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToFavourite: () -> Unit = {
        navController.navigate(Destinations.Favourite.route) {}
    }


    val navigateToSakuraDetail: (String) -> Unit = {
        navController.navigate(Destinations.SakuraDetail.createRoute(Uri.encode(it))) {
        }
    }

    val navigateToNaifeiDetail: (Int) -> Unit = {
        navController.navigateAndArgument(
            Destinations.NaifeiDetail.route,
            listOf(Pair("vod_id", it)),
        )
    }

    val navigationToMore: (String) -> Unit = {
        navController.navigate(Destinations.More.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}


fun NavController.navigateAndArgument(
    route: String,
    args: List<Pair<String, Any>>? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
) {
    navigate(route = route, navOptions = navOptions, navigatorExtras = navigatorExtras)

    if (args == null && args?.isEmpty() == true) {
        return
    }

    val bundle = backQueue.lastOrNull()?.arguments
    if (bundle != null) {
        bundle.putAll(bundleOf(*args?.toTypedArray()!!))
    } else {
        println("The last argument of NavBackStackEntry is NULL")
    }
}