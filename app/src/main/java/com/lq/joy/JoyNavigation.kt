package com.lq.joy

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

object JoyDestinations {
    const val HOME = "home"
    const val SEARCH = "search"

    const val DETAIL = "detail"
    const val DETAIL_PARAMS_ID = "id"
    const val DETAIL_WITH_PARAMS = "${DETAIL}/{${DETAIL_PARAMS_ID}}"

    const val MORE = "more"
}


class NavigationActions(navController: NavController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(JoyDestinations.HOME) {
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
        navController.navigate(JoyDestinations.SEARCH) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToDetail: (String) -> Unit = {
        navController.navigate("${JoyDestinations.DETAIL}/$it") {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigationToMore: (String) -> Unit = {
        navController.navigate(JoyDestinations.MORE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}