package com.lq.joy.ui.page.detail

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lq.joy.LockScreenOrientation
import com.lq.joy.TAG
import com.lq.joy.ui.page.common.CenterLoadingContent
import com.lq.joy.ui.theme.Grey500
import com.lq.joy.ui.theme.VipYellow

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
//            Text(text = "无数据", modifier = Modifier.align(Alignment.Center))
        }) {
        //https://stackoverflow.com/questions/69558033/kotlin-error-smart-cast-to-x-is-impossible-because-state-is-a-property-that
        val localUiState: DetailUiState = uiState
        check(localUiState is DetailUiState.HasData)

        val playUrl = localUiState.getCurrentPlayUrl()

        if (playUrl != null) {
            LaunchedEffect(key1 = playUrl, key2 = localUiState.currentIndex) {
                videoController.setSource(playUrl)
            }
        }

        if (!isExpandedScreen) {
            VideoViewWithDetail(
                videoController,
                localUiState,
                onCoverClick = { viewModel.getUrlAndPlay(0) },
                onEpisodeSelected = {
                    videoController.reset()
                    viewModel.getUrlAndPlay(it)
                }
            )
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

@Composable
private fun VideoViewWithDetail(
    videoController: DefaultVideoController,
    localUiState: DetailUiState.HasData,
    onCoverClick: () -> Unit,
    onEpisodeSelected: (Int) -> Unit

) {
    val width = LocalConfiguration.current.screenWidthDp
    val height = (9 * width) / 16
    Column(modifier = Modifier.fillMaxSize()) {
        if (localUiState.currentIndex != -1) {
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
                    .clickable { onCoverClick() }
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

                Icon(
                    imageVector = Icons.Rounded.PlayCircle, contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(70.dp, 70.dp)
                        .padding(bottom = 16.dp, end = 16.dp)
                )
            }
        }

        EpisodeDetails(localUiState, localUiState.isFavorite, onEpisodeSelected = onEpisodeSelected)

    }
}

@Composable
private fun EpisodeDetails(
    uiState: DetailUiState.HasData,
    isFavorite: Boolean,
    onEpisodeSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiState.data.animationName, color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(start = 16.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )

            IconToggleButton(
                checked = isFavorite,
                onCheckedChange = { }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else MaterialTheme.colors.onSurface
                )

            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "共${uiState.data.episodes.size}集",
                color = Grey500,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp)
            )

            Text(
                text = "${uiState.data.score}分", color = VipYellow,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "详情", color = Grey500,
                        fontSize = 14.sp,
                    )
                    Icon(
                        imageVector = Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = Grey500
                    )
                }
            }
        }

        Text(
            text = "选集", color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        LazyRow {
            itemsIndexed(uiState.data.episodes) { index, episode ->
                Spacer(modifier = Modifier.width(5.dp))
                Row(
                    modifier = Modifier
                        .clickable {
                            onEpisodeSelected(index)
                        }
                        .wrapContentWidth()
                        .height(50.dp)
                        .border(
                            width = 1.dp,
                            if (uiState.currentIndex == index) MaterialTheme.colors.primaryVariant else Grey500,
                            shape = RoundedCornerShape(5.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = episode.episodeName, color = MaterialTheme.colors.onSurface)
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
    }
}