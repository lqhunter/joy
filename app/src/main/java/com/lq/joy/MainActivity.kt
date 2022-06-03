package com.lq.joy

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lq.joy.ui.theme.Blue100
import com.lq.joy.ui.theme.JoyTheme
import com.lq.joy.utils.WindowSize
import com.lq.joy.utils.rememberWindowSizeClass

const val TAG = "MyJoy"

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as JoyApplication).container

        setContent {
            JoyTheme {

                val windowSizeClass = rememberWindowSizeClass()
                val isExpandedScreen = windowSizeClass == WindowSize.Expanded

                val systemUiController = rememberSystemUiController()
                val darkIcons = MaterialTheme.colors.isLight
                SideEffect {
                    Log.d(TAG, "setSystemBarsColor")
                    systemUiController.setSystemBarsColor(Blue100, darkIcons = darkIcons)
                }

                val navController = rememberNavController()
                val navigationActions = remember(navController) {
                    NavigationActions(navController)
                }

                JoyNavGraph(appContainer, isExpandedScreen, navController, navigationActions)
            }
        }
    }
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}