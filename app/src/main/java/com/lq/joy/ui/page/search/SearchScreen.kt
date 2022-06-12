@file:OptIn(ExperimentalFoundationApi::class)

package com.lq.joy.ui.page.search

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.lq.joy.TAG
import com.lq.joy.data.ui.VideoSearchBean
import com.lq.joy.ui.page.common.ItemRow
import com.lq.joy.utils.isScrolled

val tabs = mutableListOf(
    "影视",
    "小说",
    "漫画"
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel, onVideoSelected: (VideoSearchBean) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    var searchContent by remember {
        mutableStateOf(uiState.key)
    }
    var selectedIndex by remember {
        mutableStateOf(0)
    }

    val isScroll by remember {
        lazyListState.isScrolled
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {

        Surface(elevation = if (!isScroll) 0.dp else 4.dp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                SearchView(
                    value = searchContent,
                    onValueChange = { searchContent = it },
                    onSearch = { viewModel.search(it) },
                    modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                )
            }
        }


        if (uiState.reSearch) {
            val dataNaifei = viewModel.naifeiSearchFlow!!.collectAsLazyPagingItems()
            val dataSakura = viewModel.sakuraSearchFlow!!.collectAsLazyPagingItems()
            LazyColumn(state = lazyListState) {
                //后续增加tab切换功能再放开
                /*item {
                    TabRow(
                        backgroundColor = MaterialTheme.colors.surface,
                        selectedTabIndex = selectedIndex,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        divider = {
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(MaterialTheme.colors.surface)
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, s ->
                            Tab(
                                selected = selectedIndex == index,
                                onClick = { selectedIndex = index },
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = s,
                                    style = TextStyle(color = MaterialTheme.colors.onBackground)
                                )
                            }
                        }
                    }
                }*/

                if (dataNaifei.itemCount != 0) {
                    stickyHeader {
                        Surface(elevation = if (!isScroll) 0.dp else 4.dp) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "奈飞")
                            }
                        }
                    }
                }

                items(dataNaifei) { item ->
                    if (item is VideoSearchBean.NaifeiBean) {
                        Column(modifier = Modifier.clickable {
                            onVideoSelected(item)
                        }) {
                            Spacer(modifier = Modifier.padding(5.dp))
                            ItemRow(
                                item = item,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                            Spacer(modifier = Modifier.padding(5.dp))
                            Divider(thickness = Dp.Hairline)
                        }
                    }
                }

                dataNaifei.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                Log.d(TAG, "loadState.refresh is LoadState.Loading")
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        loadState.refresh is LoadState.Error -> {

                        }
                        loadState.append is LoadState.Loading -> {
                            item {
                                Log.d(TAG, "loadState.append is LoadState.Loading")
                                CircularProgressIndicator()
                            }
                        }
                        loadState.append is LoadState.Error -> {

                        }
                        loadState.append is LoadState.NotLoading -> {
                            val e = dataNaifei.loadState.append as LoadState.NotLoading

                        }
                    }
                }

                if (dataNaifei.itemCount == 0 || dataNaifei.loadState.append !is LoadState.NotLoading) {
                    return@LazyColumn
                }


                stickyHeader {
                    Surface(elevation = if (!isScroll) 0.dp else 4.dp) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "樱花动漫")
                        }
                    }
                }


                items(dataSakura) { item ->
                    if (item is VideoSearchBean.SakuraBean) {
                        Column(modifier = Modifier.clickable {

                        }) {
                            Spacer(modifier = Modifier.padding(5.dp))
                            ItemRow(
                                item = item,
                                modifier = Modifier.padding(start = 5.dp),
                                onClick = {}
                            )
                            Spacer(modifier = Modifier.padding(5.dp))
                            Divider(thickness = Dp.Hairline)
                        }
                    }
                }

                dataSakura.apply {
                    when {
                        loadState.refresh is LoadState.Error -> {

                        }
                        loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading -> {
                            item {
                                CircularProgressIndicator()
                            }
                        }
                        loadState.append is LoadState.Error -> {

                        }
                        loadState.append is LoadState.NotLoading -> {

                        }
                    }
                }
            }
        }


    }

}

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val width = screenWidth * 0.9
    val focusRequester = remember { FocusRequester() }
    Card(
        elevation = 5.dp, shape = RoundedCornerShape(25.dp), modifier = modifier
            .height(50.dp)
            .width(width.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = MaterialTheme.colors.onBackground, fontSize = 16.sp),
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colors.onBackground),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        innerTextField()
                    }

                    Row(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Rounded.FilterAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                            )
                        }
                    }


                }

            },
            keyboardActions = KeyboardActions(
                onDone = {
                    onSearch(value)
                    focusManager.clearFocus()
                }
            )
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

}
