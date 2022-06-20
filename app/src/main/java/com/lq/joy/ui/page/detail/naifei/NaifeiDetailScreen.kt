package com.lq.joy.ui.page.detail.naifei

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lq.joy.LockScreenOrientation
import com.lq.joy.TAG
import com.lq.joy.data.sakura.bean.PlayBean
import com.lq.joy.data.sakura.bean.VideoSource
import com.lq.joy.ui.page.common.CenterLoadingContent
import com.lq.joy.ui.page.detail.DefaultVideoController
import com.lq.joy.ui.page.detail.VideoPlayer
import com.lq.joy.ui.page.detail.rememberVideoController
import com.lq.joy.ui.theme.Grey500


@Composable
fun NaifeiDetailScreen(
    viewModel: NaifeiDetailViewModel,
    isExpandedScreen: Boolean,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onRecommendClick: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val videoController = rememberVideoController()
    val videoPlayerState by videoController.state.collectAsState()

    if (videoPlayerState.isReady) {
        LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
    }

    CenterLoadingContent(
        isLoading = false,
        isEmpty = false,
        modifier = Modifier.fillMaxSize(),
        contentEmpty = {

        }) {
        //https://stackoverflow.com/questions/69558033/kotlin-error-smart-cast-to-x-is-impossible-because-state-is-a-property-that


        if (!isExpandedScreen) {
            VideoViewWithEpisode(
                videoController = videoController,
                videoSource = uiState.videoSearchBean.videoSources,
                currentSourceIndex = uiState.currentSourceIndex,
                currentEpisodeIndex = uiState.currentEpisodeIndex,
                coverUrl = uiState.videoSearchBean.coverUrl,
                onEpisodeSelected = { sourceIndex, index, playBean ->
                    playBean.playUrl?.let {
                        videoController.setSource(it)
                    }
                    viewModel.selectIndex(sourceIndex, index)
                },
                onRecommendClick = { }
            ) {

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VideoViewWithEpisode(
    videoController: DefaultVideoController,
    videoSource: List<VideoSource>,
    currentSourceIndex: Int,
    currentEpisodeIndex: Int,
    coverUrl: String,
    onEpisodeSelected: (Int, Int, PlayBean) -> Unit,
    onRecommendClick: (String) -> Unit,
    episodeIntroduce: @Composable () -> Unit
) {
    val width = LocalConfiguration.current.screenWidthDp
    val height = (9 * width) / 16
    Column(modifier = Modifier.fillMaxSize()) {

        if (currentEpisodeIndex != -1) {
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
                    .clickable {
                        if (videoSource.isNotEmpty()) {
                            val episode = videoSource[0].episodes
                            if (episode.isNotEmpty()) {
                                val playBean = episode[0]
                                onEpisodeSelected(0, 0, playBean)

                            }
                        }
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
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


        LazyColumn(modifier = Modifier.background(MaterialTheme.colors.background)) {
            item {
                Spacer(modifier = Modifier.padding(5.dp))
                episodeIntroduce()

            }

            itemsIndexed(videoSource) { index: Int, item: VideoSource ->
                EpisodeSelector(
                    playBean = item.episodes,
                    onEpisodeSelected = { i, playBean ->
                        onEpisodeSelected(index, i, playBean)
                    },
                    currentSelected = if (currentSourceIndex == index) currentEpisodeIndex else -1
                )
                Spacer(modifier = Modifier.padding(10.dp))

            }
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background),
                ) {
                    Text(
                        text = "推荐", color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

            }
        }
    }
}

@Composable
fun EpisodeSelector(
    playBean: List<PlayBean>,
    onEpisodeSelected: (Int, PlayBean) -> Unit,
    currentSelected: Int = -1,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "选集", color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        LazyRow {
            item {
                Spacer(modifier = Modifier.width(16.dp))
            }
            itemsIndexed(playBean) { index, episode ->
                Row(
                    modifier = Modifier
                        .clickable {
                            onEpisodeSelected(index, episode)
                        }
                        .wrapContentWidth()
                        .height(50.dp)
                        .border(
                            width = 1.dp,
                            if (currentSelected == index) MaterialTheme.colors.secondaryVariant else Grey500,
                            shape = RoundedCornerShape(5.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = episode.episodeName, color = MaterialTheme.colors.onSurface)
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}
