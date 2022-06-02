package com.lq.joy

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

sealed class Destinations(val route: String) {
    object Home : Destinations("home")
    object Search : Destinations("search")
    object More : Destinations("more")

    object Detail : Destinations("detail/{episodeUri}") {
        fun createRoute(episodeUri: String) = "detail/$episodeUri"
    }
}


class NavigationActions(navController: NavController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(Destinations.Home.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
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

    val navigateToDetail: (String) -> Unit = {
        navController.navigate(Destinations.Detail.createRoute(Uri.encode(it))) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
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