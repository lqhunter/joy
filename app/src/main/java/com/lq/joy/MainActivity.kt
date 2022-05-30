package com.lq.joy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lq.joy.ui.home.HomeScreen
import com.lq.joy.ui.theme.Blue50
import com.lq.joy.ui.theme.JoyTheme

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        val appContainer = (application as JoyApplication).container

        setContent {
            JoyTheme {
                val systemUiController = rememberSystemUiController()
                val darkIcons = MaterialTheme.colors.isLight
                SideEffect {
                    systemUiController.setSystemBarsColor(Blue50, darkIcons = darkIcons)
                }

                HomeScreen(appContainer)

            }
        }
    }


}