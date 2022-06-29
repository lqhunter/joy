package com.lq.joy.ui.page.detail.naifei

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.exoplayer2.MediaItem
import com.lq.joy.LockScreenOrientation
import com.lq.joy.R
import com.lq.joy.TAG
import com.lq.joy.data.Api
import com.lq.joy.data.netfix.bean.NaifeiDetailBean
import com.lq.joy.data.sakura.bean.HomeItemBean
import com.lq.joy.ui.page.common.CenterLoadingContent
import com.lq.joy.ui.page.detail.DefaultVideoController
import com.lq.joy.ui.page.detail.VideoPlayer
import com.lq.joy.ui.page.detail.rememberVideoController
import com.lq.joy.ui.theme.Grey500
import retrofit2.Response.error


@Composable
fun NaifeiDetailScreen(
    viewModel: NaifeiDetailViewModel,
    isExpandedScreen: Boolean,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    systemUiController: SystemUiController = rememberSystemUiController(),
    onRecommendClick: (Int) -> Unit,
    finish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val _uiState = uiState

    val videoController = rememberVideoController()
    val videoPlayerState by videoController.state.collectAsState()

    if (videoPlayerState.isReady) {
        LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
    }

    if (videoPlayerState.episodeIndex != -1) {
        LaunchedEffect(key1 = videoPlayerState.episodeIndex) {
            viewModel.selectIndex(videoPlayerState.episodeIndex)
        }
    }


    var rowLazyState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (_uiState is NaifeiDetailUiState.HasData) {
            if (_uiState.currentEpisodeIndex == -1) 0 else _uiState.currentEpisodeIndex
        } else 0
    )

    CenterLoadingContent(
        isLoading = uiState.isLoading,
        isEmpty = uiState is NaifeiDetailUiState.NoData,
        modifier = Modifier.fillMaxSize(),
        contentEmpty = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Image(painterResource(id = R.drawable.error), contentDescription = "")
                Text(text = "出错了...")
            }
        }) {
        //https://stackoverflow.com/questions/69558033/kotlin-error-smart-cast-to-x-is-impossible-because-state-is-a-property-that
        check(_uiState is NaifeiDetailUiState.HasData)

        LaunchedEffect(key1 = isExpandedScreen) {
            systemUiController.isSystemBarsVisible = !isExpandedScreen
        }

        if (!isExpandedScreen) {
            VideoViewWithDetail(
                videoController = videoController,
                videoSource = _uiState.videoSource,
                currentEpisodeIndex = _uiState.currentEpisodeIndex,
                recommend = _uiState.recommend,
                coverUrl = _uiState.coverUrl,
                onEpisodeSelected = { index, playBean ->
                    if (videoController.getItemsCount() == 0) {
                        videoController.setItems(_uiState.videoSource.urls.mapIndexed { i, it ->
                            MediaItem.Builder().setUri(it.url).setTag(i).build()
                        }, index)
                    } else {
                        videoController.seekTo(index, 0)
                    }
                    viewModel.selectIndex(index)
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
            Log.d(TAG, "LifecycleEvent:onDispose")
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VideoViewWithDetail(
    videoController: DefaultVideoController,
    videoSource: NaifeiDetailBean.Data.VodPlay,
    recommend: List<NaifeiDetailBean.Data.RelVod>,
    currentEpisodeIndex: Int,
    coverUrl: String,
    onEpisodeSelected: (Int, NaifeiDetailBean.Data.VodPlay.Url) -> Unit,
    onRecommendClick: (Int) -> Unit,
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
                        val playBean = videoSource.urls[0]
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
                        playBean = videoSource.urls,
                        onEpisodeSelected = { i, playBean ->
                            onEpisodeSelected(i, playBean)
                        },
                        currentSelected = currentEpisodeIndex,
                        onEpisodeExpend = {
                            isEpisodeExpend = true
                        },
                        state = rowLazyState
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

                itemsIndexed(recommend) { index, item ->
                    ItemRow(
                        item = item,
                        onClick = onRecommendClick,
                        modifier = Modifier.padding(start = 16.dp)
                    )
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
                itemsIndexed(videoSource.urls) { index, episode ->
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
                        Text(text = episode.name, color = MaterialTheme.colors.onSurface)
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
fun EpisodeSelector(
    playBean: List<NaifeiDetailBean.Data.VodPlay.Url>,
    onEpisodeSelected: (Int, NaifeiDetailBean.Data.VodPlay.Url) -> Unit,
    currentSelected: Int = -1,
    onEpisodeExpend: () -> Unit,
    state: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onEpisodeExpend()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "选集", color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Icon(imageVector = Icons.Rounded.NavigateNext, contentDescription = "")
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
                            if (currentSelected == index) MaterialTheme.colors.secondaryVariant else Grey500,
                            shape = RoundedCornerShape(5.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = episode.name, color = MaterialTheme.colors.onSurface)
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        LaunchedEffect(key1 = currentSelected) {
            if (currentSelected != -1) {
                Log.d(TAG, "currentSelected:${currentSelected}")
                var needScroll = true
                for (lazyListItemInfo in state.layoutInfo.visibleItemsInfo) {
                    Log.d(TAG, "lazyListItemInfo.index:${lazyListItemInfo.index}")
                    if (lazyListItemInfo.index == currentSelected) {
                        Log.d(TAG, "need scroll false")
                        needScroll = false
                        break
                    }
                }
                if (needScroll)
                    state.animateScrollToItem(currentSelected)
            }
        }
    }
}

@Composable
private fun ItemRow(
    item: NaifeiDetailBean.Data.RelVod,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Row(modifier = modifier
        .clickable {
            onClick(item.vod_id)
        }
        .height(100.dp)
        .padding(top = 10.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically)
    {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Api.NAIFEI_HOST + "/" + item.vod_pic)
                .crossfade(true)
                .build(),
            contentDescription = item.vod_name,
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
                text = item.vod_name,
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
                text = "类型：${item.vod_tag}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        }


    }
}
