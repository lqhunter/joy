package com.lq.joy.ui.page.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lq.joy.ui.page.common.CenterLoadingContent

@Composable
fun DetailScreen(viewModel: DetailViewModel) {
    val uiState by viewModel.uiState.collectAsState()

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


        Column {
            /*localUiState.videoPlayerState?.let {
                VideoPlayer(
                    modifier = Modifier
                        .background(Color.Black)
                        .width(width.dp)
                        .height(height.dp),
                    videoPlayerState = it,
                    onSeekChange = {},
                    onPlayOrPause = {}
                )

            } ?: let {

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

                    IconButton(onClick = { viewModel.getUrlAndPlay(0) }) {
                        Image(
                            imageVector = Icons.Filled.PlayCircleFilled, contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 16.dp, end = 16.dp)
                        )
                    }
                }


            }*/


        }
    }

}