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
import com.lq.joy.data.AppContainer
import com.lq.lib_sakura.bean.HomeItemBean

@Composable
fun HomeScreen(appContainer: AppContainer) {
    val viewModel: HomeViewModel =
        viewModel(factory = HomeViewModel.providerFactory(appContainer.sakuraRepository))

    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is HomeUiState.HasData -> {
            //todo 写一个banner

            HomeContentHasData(uiState)


        }
        is HomeUiState.NoData -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(text = "无数据", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun HomeScreenScaffold(
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { snackbarHostState ->

        },
        topBar = {

        }


    ) {

    }


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeContentHasData(uiState: HomeUiState) {
    Box(modifier = Modifier.fillMaxSize()) {
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
                            .background(Color.White)
                    ) {
                        TextButton(onClick = { /*TODO*/ }) {
                            Text(text = group.groupTitle)
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
    Card(elevation = 4.dp, modifier = modifier.clickable {
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
                color = Color.Black,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}