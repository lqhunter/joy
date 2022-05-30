package com.lq.joy.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.lq.joy.data.AppContainer
import com.lq.lib_sakura.bean.HomeItemBean

@Composable
fun HomeScreen(appContainer: AppContainer) {
    val viewModel: HomeViewModel =
        viewModel(factory = HomeViewModel.providerFactory(appContainer.sakuraRepository))

    val uiState by viewModel.uiState.collectAsState()

    HomeScreenScaffold(uiState = uiState,
        onRefresh = { viewModel.getHomeHtml() })

}

@Composable
fun HomeScreenScaffold(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onRefresh: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { },
        topBar = {

        },
        modifier = modifier
    ) { innerPadding ->
        val contentPadding = Modifier.padding(innerPadding)
        LoadingContent(
            isLoading = uiState.isLoading,
            empty = when (uiState) {
                is HomeUiState.HasData -> false
                is HomeUiState.NoData -> true
            },
            onRefresh = onRefresh,
            contentEmpty = {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(text = "无数据", modifier = Modifier.align(Alignment.Center))
                }
            }) {
            HomeContentHasData(uiState)
        }
    }
}


@Composable
fun HomeTopBar() {


}

@Composable
fun LoadingContent(
    isLoading: Boolean,
    empty: Boolean,
    onRefresh: () -> Unit,
    contentEmpty: @Composable () -> Unit,
    contentHasData: @Composable () -> Unit
) {
    println("refresh $isLoading")
    SwipeRefresh(
        state = rememberSwipeRefreshState(isLoading),
        onRefresh = onRefresh,
    ) {
        if (empty) {
            contentEmpty()
        } else {
            contentHasData()
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeContentHasData(uiState: HomeUiState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        val width = LocalConfiguration.current.screenWidthDp
        val verticalCount = 2
        val itemWidthPadding = 8
        val itemWidth =
            (width - itemWidthPadding * (2 + 2 * (verticalCount - 1))) / verticalCount
        val itemHeight = (4 * itemWidth) / 3
        val textHeight = 30


        val groups = (uiState as HomeUiState.HasData).data.groups
        LazyColumn {
            //todo 这种循环的方式估计会影响性能。是否真正是懒加载
            groups.forEach { group ->

                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background),
                    ) {
                        Text(
                            text = group.groupTitle,
                            color = MaterialTheme.colors.onBackground,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .align(Alignment.CenterStart)
                        )

                        TextButton(onClick = {

                        }, modifier = Modifier.align(Alignment.CenterEnd)) {
                            Text(text = "更多", color = MaterialTheme.colors.primary)
                        }
                    }
                }

                val size = group.items.size
                for (i in 0 until size step verticalCount) {
                    item {
                        Row {
                            for (j in 0 until verticalCount) {
                                val item = group.items[i + j]
                                HomeItem(
                                    item = item,
                                    modifier = Modifier
                                        .padding(itemWidthPadding.dp)
                                        .width(itemWidth.dp)
                                        .height((itemHeight + textHeight).dp),
                                    imageWidth = itemWidth,
                                    imageHeight = itemHeight
                                ) {


                                }

                            }
                        }
                    }

                }

            }
        }

    }

}

@Composable
private fun HomeItem(
    item: HomeItemBean,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(elevation = 1.dp, modifier = modifier.clickable {
        onClick()
    }) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                modifier = Modifier
                    .width(imageWidth.dp)
                    .height(imageHeight.dp),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = item.name,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}