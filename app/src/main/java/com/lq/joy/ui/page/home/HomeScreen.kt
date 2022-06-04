package com.lq.joy.ui.page.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lq.joy.R
import com.lq.joy.data.AppContainer
import com.lq.joy.data.sakura.bean.HomeItemBean
import com.lq.joy.ui.page.common.ItemColumn
import com.lq.joy.ui.page.common.SwipeRefreshContent
import com.lq.joy.ui.theme.Blue500

@Composable
fun HomeScreen(
    appContainer: AppContainer,
    onSearchClick: () -> Unit,
    onAnimationClick: (HomeItemBean) -> Unit,
    onMoreClick: (String) -> Unit
    ) {
    val viewModel: HomeViewModel =
        viewModel(factory = HomeViewModel.providerFactory(appContainer.sakuraRepository))

    val uiState by viewModel.uiState.collectAsState()

    HomeScreenScaffold(
        uiState = uiState,
        onRefresh = { viewModel.getHomeHtml() },
        onSearchClick = onSearchClick,
        onAnimationClick = onAnimationClick,
        onMoreClick = onMoreClick
    )

}

@Composable
fun HomeScreenScaffold(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onSearchClick: () -> Unit,
    onAnimationClick: (HomeItemBean) -> Unit,
    onMoreClick: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { },
        topBar = {
            HomeTopBar(onSearchClick)
        },
        modifier = modifier
    ) { innerPadding ->
        val contentPadding = Modifier.padding(innerPadding)
        SwipeRefreshContent(
            isLoading = uiState.isLoading,
            isEmpty = when (uiState) {
                is HomeUiState.HasData -> false
                is HomeUiState.NoData -> true
            },
            onRefresh = onRefresh,
            contentEmpty = {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(text = "无数据", modifier = Modifier.align(Alignment.Center))
                }
            }) {


            HomeContentHasData(uiState, onAnimationClick, onMoreClick)
        }
    }
}


@Composable
fun HomeTopBar(onSearchClick: () -> Unit) {

    Box(modifier = Modifier.fillMaxWidth().height(56.dp).background(MaterialTheme.colors.primary)) {
        Text(
            text = stringResource(id = R.string.sakura),
            modifier = Modifier.align(Alignment.Center),
            fontSize = 20.sp
        )

        IconButton(onClick = onSearchClick, modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeContentHasData(
    uiState: HomeUiState,
    onAnimationClick: (HomeItemBean) -> Unit,
    onMoreClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val width = LocalConfiguration.current.screenWidthDp
        val verticalCount = 2
        val itemWidthPadding = 8
        val itemWidth =
            (width - itemWidthPadding * (2 + 2 * (verticalCount - 1))) / verticalCount
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
                            onMoreClick(group.groupUrl)
                        }, modifier = Modifier.align(Alignment.CenterEnd)) {
                            Text(text = "更多", color = MaterialTheme.colors.secondary)
                        }
                    }
                }

                val size = group.items.size
                for (i in 0 until size step verticalCount) {
                    item {
                        Row {
                            for (j in 0 until verticalCount) {
                                ItemColumn(
                                    item = group.items[i + j],
                                    modifier = Modifier
                                        .padding(itemWidthPadding.dp)
                                        .width(itemWidth.dp)
                                        .wrapContentHeight(),
                                    onClick = onAnimationClick
                                )
                            }
                        }
                    }

                }

            }
        }

    }

}