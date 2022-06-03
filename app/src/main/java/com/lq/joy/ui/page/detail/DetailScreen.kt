package com.lq.joy.ui.page.detail

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lq.joy.LockScreenOrientation
import com.lq.joy.TAG
import com.lq.joy.ui.page.common.CenterLoadingContent

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    isExpandedScreen: Boolean,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val uiState by viewModel.uiState.collectAsState()

    val videoController = rememberVideoController()
    val videoPlayerState by videoController.state.collectAsState()

    val systemUiController = rememberSystemUiController()

    if (videoPlayerState.isReady) {
        LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
    }

    CenterLoadingContent(
        isLoading = uiState.isLoading,
        isEmpty = when (uiState) {
            is DetailUiState.HasData -> false
            is DetailUiState.NoData -> true
        },
        modifier = Modifier.fillMaxSize(),
        contentEmpty = {
            Text(text = "无数据", modifier = Modifier.align(Alignment.Center))
        }) {
        //https://stackoverflow.com/questions/69558033/kotlin-error-smart-cast-to-x-is-impossible-because-state-is-a-property-that
        val localUiState: DetailUiState = uiState
        check(localUiState is DetailUiState.HasData)

        val width = LocalConfiguration.current.screenWidthDp
        val height = (9 * width) / 16

        val playUrl = localUiState.getCurrentPlayUrl()

        if (playUrl != null) {
            LaunchedEffect(key1 = playUrl) {
                videoController.setSource(playUrl)
            }
        }

        if (!isExpandedScreen) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (playUrl != null) {
                    VideoPlayer(
                        modifier = Modifier
                            .background(Color.Black)
                            .width(width.dp)
                            .height(height.dp),
                        videoController = videoController,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .width(width.dp)
                            .height(height.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(localUiState.data.coverUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = localUiState.data.animationName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillHeight
                        )

                        IconButton(
                            onClick = { viewModel.getUrlAndPlay(0) }, modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(70.dp, 70.dp)
                                .padding(bottom = 16.dp, end = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayCircle, contentDescription = null,
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

            }
        } else {
            VideoPlayer(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize(),
                videoController = videoController,
            )
        }


        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                Log.d(TAG, "LifecycleEvent:$event")

                when (event) {
                    Lifecycle.Event.ON_CREATE -> {

                    }
                    Lifecycle.Event.ON_RESUME -> {
                        videoController.play()
                    }
                    Lifecycle.Event.ON_PAUSE -> {

                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        videoController.release()
                    }
                }
            }

            // Add the observer to the lifecycle
            lifecycleOwner.lifecycle.addObserver(observer)

            // When the effect leaves the Composition, remove the observer
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }


}