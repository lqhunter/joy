package com.lq.joy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import com.lq.joy.ui.home.HomeScreen
import com.lq.joy.ui.theme.JoyTheme

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as JoyApplication).container

        setContent {
            JoyTheme {
                HomeScreen(appContainer)

            }
        }
    }


}
