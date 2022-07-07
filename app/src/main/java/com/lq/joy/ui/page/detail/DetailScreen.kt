package com.lq.joy.ui.page.detail

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.exoplayer2.MediaItem
import com.lq.joy.TAG
import com.lq.joy.data.SourceType
import com.lq.joy.data.ui.PlayBean
import com.lq.joy.data.ui.RecommendBean
import com.lq.joy.data.ui.VideoSource
import com.lq.joy.findActivity
import com.lq.joy.ui.page.common.CenterLoadingContent
import com.lq.joy.ui.page.common.SourceSelectorItem
import com.lq.joy.ui.page.common.SourceUiType
import com.lq.joy.ui.theme.Grey500
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    detailType: SourceType,
    viewModel: ViewModel,
    isExpandedScreen: Boolean,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    systemUiController: SystemUiController = rememberSystemUiController(),
    onRecommendClick: (RecommendBean) -> Unit,
    finish: () -> Unit,
    originalOrientation: Int
) {
    val videoController = rememberVideoController()
    val videoPlayerState by videoController.state.collectAsState()
    val context = LocalContext.current

    context.findActivity()?.let { activity ->
        LaunchedEffect(key1 = videoPlayerState.isReady, key2 = videoPlayerState.lockLandscape) {
            if (videoPlayerState.isReady) {
                if (videoPlayerState.lockLandscape) {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                } else {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                }
            }
        }
    }

    when (detailType) {
        SourceType.NAIFEI -> {
            check(viewModel is NaifeiDetailViewModel)
            DetailScreenNaifei(
                viewModel = viewModel,
                isExpandedScreen = isExpandedScreen,
                videoPlayerState = videoPlayerState,
                videoController = videoController,
                systemUiController = systemUiController,
                onRecommendClick = onRecommendClick,
                finish = finish
            )

        }
        SourceType.SAKURA -> {
            check(viewModel is SakuraDetailViewModel)
            DetailScreenSakura(
                viewModel = viewModel,
                isExpandedScreen = isExpandedScreen,
                videoPlayerState = videoPlayerState,
                videoController = videoController,
                systemUiController = systemUiController,
                onRecommendClick = onRecommendClick,
                finish = finish
            )
        }
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
                    videoController.pause()
                }
                Lifecycle.Event.ON_DESTROY -> {

                }
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            context.findActivity()?.requestedOrientation = originalOrientation
        }
    }

}

@Composable
fun DetailScreenNaifei(
    viewModel: NaifeiDetailViewModel,
    isExpandedScreen: Boolean,
    videoPlayerState: VideoPlayerState,
    videoController: DefaultVideoController,
    systemUiController: SystemUiController,
    onRecommendClick: (RecommendBean) -> Unit,
    finish: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val _uiState = uiState

    if (videoPlayerState.episodeIndex != -1) {
        LaunchedEffect(key1 = videoPlayerState.episodeIndex) {
            viewModel.selectEpisode(videoPlayerState.episodeIndex)
        }
    }

    val rowLazyState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (_uiState is DetailUiState.HasData) {
            if (_uiState.currentEpisodeIndex == -1) 0 else _uiState.currentEpisodeIndex
        } else 0
    )

    CenterLoadingContent(
        isLoading = uiState.isLoading,
        isEmpty = uiState is DetailUiState.NoData,
        modifier = Modifier.fillMaxSize(),
        contentEmpty = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "出错了...")
            }
        }) {
        //https://stackoverflow.com/questions/69558033/kotlin-error-smart-cast-to-x-is-impossible-because-state-is-a-property-that
        check(_uiState is DetailUiState.HasData)

        LaunchedEffect(key1 = _uiState.currentEpisodeIndex) {
            if (_uiState.currentEpisodeIndex == -1) {
                videoController.reset()
            }
        }

        LaunchedEffect(key1 = isExpandedScreen) {
            systemUiController.isSystemBarsVisible = !isExpandedScreen
            videoController.setLockShow(isExpandedScreen)
        }

        if (!isExpandedScreen) {
            VideoViewWithDetail(
                videoController = videoController,
                videoSource = _uiState.videoSource,
                currentEpisodeIndex = _uiState.currentEpisodeIndex,
                currentSourceIndex = _uiState.currentSourceIndex,
                recommend = _uiState.recommend,
                coverUrl = _uiState.coverUrl,
                onSourceSelected = {
                    viewModel.selectSource(it)

                },
                onEpisodeSelected = { index, playBean ->
                    if (_uiState.currentEpisodeIndex == -1) {
                        videoController.setItems(_uiState.videoSource[_uiState.currentSourceIndex].episodes.mapIndexed { i, it ->
                            MediaItem.Builder().setUri(it.playUrl).setTag(i).build()
                        }, index)
                    } else {
                        videoController.seekTo(index, 0)
                    }
                    viewModel.selectEpisode(index)
                },
                onRecommendClick = onRecommendClick,
                episodeIntroduce = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Text(
                            text = _uiState.name, color = MaterialTheme.colors.onSurface,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )

                        IconToggleButton(
                            checked = _uiState.isFavorite,
                            onCheckedChange = {
                            }
                        ) {
                            Icon(
                                imageVector = if (_uiState.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = null,
                                tint = if (_uiState.isFavorite) Color.Red else MaterialTheme.colors.onSurface
                            )

                        }
                    }
                },
                rowLazyState = rowLazyState,
                finish = {
                    videoController.release()
                    finish()
                },
            )
        } else {
            VideoPlayer(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize(),
                videoController = videoController,
            )
        }
    }
}

@Composable
fun DetailScreenSakura(
    viewModel: SakuraDetailViewModel,
    isExpandedScreen: Boolean,
    videoPlayerState: VideoPlayerState,
    videoController: DefaultVideoController,
    systemUiController: SystemUiController,
    onRecommendClick: (RecommendBean) -> Unit,
    finish: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val _uiState = uiState

    if (videoPlayerState.episodeIndex != -1) {
        LaunchedEffect(key1 = videoPlayerState.episodeIndex) {
            viewModel.selectEpisode(videoPlayerState.episodeIndex)
        }
    }

    val rowLazyState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (_uiState is DetailUiState.HasData) {
            if (_uiState.currentEpisodeIndex == -1) 0 else _uiState.currentEpisodeIndex
        } else 0
    )

    val scope = rememberCoroutineScope()

    CenterLoadingContent(
        isLoading = uiState.isLoading,
        isEmpty = uiState is DetailUiState.NoData,
        modifier = Modifier.fillMaxSize(),
        contentEmpty = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "出错了...")
            }
        }) {
        //https://stackoverflow.com/questions/69558033/kotlin-error-smart-cast-to-x-is-impossible-because-state-is-a-property-that
        check(_uiState is DetailUiState.HasData)

        LaunchedEffect(key1 = _uiState.currentEpisodeIndex) {
            if (_uiState.currentEpisodeIndex == -1) {
                videoController.reset()
            }
        }

        LaunchedEffect(key1 = isExpandedScreen) {
            systemUiController.isSystemBarsVisible = !isExpandedScreen
            videoController.setLockShow(isExpandedScreen)
        }

        if (!isExpandedScreen) {
            VideoViewWithDetail(
                videoController = videoController,
                videoSource = _uiState.videoSource,
                currentEpisodeIndex = _uiState.currentEpisodeIndex,
                currentSourceIndex = _uiState.currentSourceIndex,
                recommend = _uiState.recommend,
                coverUrl = _uiState.coverUrl,
                onSourceSelected = {
                    viewModel.selectEpisode(it)

                },
                onEpisodeSelected = { index, playBean ->
                    scope.launch {
                        val url = viewModel.getPlayUrl(playBean.playUrl)
                        url?.run {
                            videoController.setItems(mutableListOf(MediaItem.Builder().setUri(this).setTag(0).build()), 0)
                        }
                    }
                    viewModel.selectEpisode(index)
                },
                onRecommendClick = onRecommendClick,
                episodeIntroduce = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Text(
                            text = _uiState.name, color = MaterialTheme.colors.onSurface,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )

                        IconToggleButton(
                            checked = _uiState.isFavorite,
                            onCheckedChange = { }
                        ) {
                            Icon(
                                imageVector = if (_uiState.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = null,
                                tint = if (_uiState.isFavorite) Color.Red else MaterialTheme.colors.onSurface
                            )

                        }
                    }
                },
                rowLazyState = rowLazyState,
                finish = {
                    videoController.release()
                    finish()
                },
            )
        } else {
            VideoPlayer(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize(),
                videoController = videoController,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VideoViewWithDetail(
    videoController: DefaultVideoController,
    videoSource: List<VideoSource>,
    recommend: List<RecommendBean>,
    currentEpisodeIndex: Int,
    currentSourceIndex: Int,
    coverUrl: String,
    onEpisodeSelected: (Int, PlayBean) -> Unit,
    onSourceSelected: (Int) -> Unit,
    onRecommendClick: (RecommendBean) -> Unit,
    episodeIntroduce: @Composable LazyItemScope.() -> Unit,
    rowLazyState: LazyListState,
    finish: () -> Unit
) {
    val width = LocalConfiguration.current.screenWidthDp
    val height = (9 * width) / 16

    var isEpisodeExpend by remember {
        mutableStateOf(false)
    }


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
                        val playBean = videoSource[currentSourceIndex].episodes[0]
                        onEpisodeSelected(0, playBean)
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
        if (!isEpisodeExpend) {
            LazyColumn(modifier = Modifier.background(MaterialTheme.colors.background)) {

                item {
                    Spacer(modifier = Modifier.padding(5.dp))
                    episodeIntroduce()
                }

                item {
                    EpisodeSelector(
                        playBean = videoSource[currentSourceIndex].episodes,
                        onEpisodeSelected = { i, playBean ->
                            onEpisodeSelected(i, playBean)
                        },
                        currentEpisodeIndex = currentEpisodeIndex,
                        currentSourceIndex = currentSourceIndex,
                        onEpisodeExpend = {
                            isEpisodeExpend = true
                        },
                        state = rowLazyState,
                        onSourceSelected = onSourceSelected,
                        videoSource = videoSource
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

                if(recommend.isNotEmpty()) {
                    itemsIndexed(recommend) { index, item ->
                        ItemRow(
                            item = item,
                            onClick = onRecommendClick,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                } else {
                    item {
                        Text(text = "没有推荐数据！\n懒得找图片，就用文字吧", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                }

            }
        } else {
            val listState =
                rememberLazyGridState(if (currentEpisodeIndex == -1) 0 else currentEpisodeIndex)
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { isEpisodeExpend = false }, modifier = Modifier.align(
                        Alignment.CenterEnd
                    )
                ) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "")
                }
            }

            LazyVerticalGrid(columns = GridCells.Fixed(2), state = listState) {
                itemsIndexed(videoSource[currentSourceIndex].episodes) { index, episode ->
                    Row(
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(
                                width = 1.dp,
                                if (currentEpisodeIndex == index) MaterialTheme.colors.secondaryVariant else Grey500,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clickable {
                                onEpisodeSelected(index, episode)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
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

    BackHandler {
        if (isEpisodeExpend) {
            isEpisodeExpend = false
        } else {
            finish()
        }
    }
}

@Composable
private fun EpisodeSelector(
    modifier: Modifier = Modifier,
    videoSource: List<VideoSource>,
    playBean: List<PlayBean>,
    onEpisodeSelected: (Int, PlayBean) -> Unit,
    onSourceSelected: (Int) -> Unit,
    currentEpisodeIndex: Int = -1,
    currentSourceIndex: Int = 0,
    onEpisodeExpend: () -> Unit,
    state: LazyListState = rememberLazyListState(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = if (playBean.size >= 10) {
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEpisodeExpend()
                    }
            } else {
                Modifier
                    .fillMaxWidth()
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "选集", color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            if (playBean.size >= 10) {
                Icon(imageVector = Icons.Rounded.NavigateNext, contentDescription = "")
            }
        }


        LazyRow(state = state) {
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
                            if (currentEpisodeIndex == index) MaterialTheme.colors.secondaryVariant else Grey500,
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

        val size = videoSource.size

        if (size > 1) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(top = 5.dp),
            ) {

                item {
                    Spacer(modifier = Modifier.width(16.dp))
                }

                itemsIndexed(videoSource) { index, item ->
                    Log.d(TAG, "ppp index:${index}")
                    SourceSelectorItem(
                        name = "线路${index + 1}",
                        isSelected = currentSourceIndex == index,
                        type = when (index) {
                            0 -> SourceUiType.FIRST
                            size - 1 -> SourceUiType.END
                            else -> SourceUiType.MID
                        },
                        modifier = Modifier
                            .width(50.dp)
                            .fillMaxHeight()
                            .clickable {
                                onSourceSelected(index)
                            }
                    )
                }
                item {
                    Spacer(modifier = Modifier.width(5.dp))
                }
            }
        }

        LaunchedEffect(key1 = currentEpisodeIndex) {
            if (currentEpisodeIndex != -1) {
                Log.d(TAG, "currentSelected:${currentEpisodeIndex}")
                var needScroll = true
                for (lazyListItemInfo in state.layoutInfo.visibleItemsInfo) {
                    Log.d(TAG, "lazyListItemInfo.index:${lazyListItemInfo.index}")
                    if (lazyListItemInfo.index == currentEpisodeIndex) {
                        Log.d(TAG, "need scroll false")
                        needScroll = false
                        break
                    }
                }
                if (needScroll)
                    state.animateScrollToItem(currentEpisodeIndex)
            }
        }
    }
}

@Composable
private fun ItemRow(
    item: RecommendBean,
    modifier: Modifier = Modifier,
    onClick: (RecommendBean) -> Unit
) {
    Row(modifier = modifier
        .clickable {
            onClick(item)
        }
        .height(100.dp)
        .padding(top = 10.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically)
    {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.cover)
                .crossfade(true)
                .build(),
            contentDescription = item.name,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = item.name,
                color = MaterialTheme.colors.onSurface,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row {
                Text(
                    text = "状态：",
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
                /*Text(text = item.newestEpisode ?: "无", color = Color.Red)*/
            }
            Text(
                text = "标签：${item.tag}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        }


    }
}