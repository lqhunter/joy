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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.lq.joy.TAG
import com.lq.joy.data.ui.VideoSearchBean
import com.lq.joy.ui.page.common.ItemRow

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNaifeiSelected: (Int) -> Unit,
    onSakuraSelected: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    var searchContent by remember {
        mutableStateOf(uiState.key)
    }

    val focusRequester = remember {
        FocusRequester()
    }

/*    val isScroll by remember {
        lazyListState.isScrolled
    }*/

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {

//        Surface(elevation = if (!isScroll) 0.dp else 4.dp) {
        Box(
            modifier = Modifier
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            SearchView(
                value = searchContent,
                onValueChange = { searchContent = it },
                focusRequester = focusRequester,
                onSearch = {
                    viewModel.search(it)
                },
                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
            )
        }
//        }

        val naifeiPaging = uiState.naifeiFlow.collectAsLazyPagingItems()
        val sakuraPaging = uiState.sakuraFlow.collectAsLazyPagingItems()

        if (naifeiPaging.loadState.refresh is LoadState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
                if (naifeiPaging.itemCount != 0) {
                    stickyHeader {
//                    Surface(elevation = if (!isScroll) 0.dp else 4.dp) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.background)
                        ) {
                            Text(
                                text = "奈飞",
                                modifier = Modifier.padding(start = 5.dp, bottom = 5.dp),
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
//                    }
                    }
                }

                items(
                    naifeiPaging,
                    key = { item: VideoSearchBean -> (item as VideoSearchBean.NaifeiBean).vodId }) { item ->
                    if (item is VideoSearchBean.NaifeiBean) {
                        Column(modifier = Modifier.clickable {
                            onNaifeiSelected(item.vodId)
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

                naifeiPaging.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {

                        }
                        loadState.refresh is LoadState.Error -> {

                        }
                        loadState.append is LoadState.Loading -> {
                            item {
                                Log.d(TAG, "loadState.append is LoadState.Loading")
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, bottom = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        loadState.append is LoadState.Error -> {

                        }
                        loadState.append is LoadState.NotLoading -> {
                            val e = naifeiPaging.loadState.append as LoadState.NotLoading

                        }
                    }
                }

                if (naifeiPaging.itemCount == 0 || naifeiPaging.loadState.append !is LoadState.NotLoading) {
                    return@LazyColumn
                }


                stickyHeader {
//                Surface(elevation = if (!isScroll) 0.dp else 4.dp*) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
                    ) {
                        Text(
                            text = "樱花动漫",
                            modifier = Modifier.padding(start = 5.dp, bottom = 5.dp),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
//                }
                }


                items(sakuraPaging) { item ->
                    if (item is VideoSearchBean.SakuraBean) {
                        Column(modifier = Modifier.clickable {
                            onSakuraSelected(item.detailUrl)
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

                sakuraPaging.apply {
                    when {
                        loadState.refresh is LoadState.Error -> {

                        }
                        loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, bottom = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
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
    focusRequester: FocusRequester = FocusRequester(),
    onValueChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val width = screenWidth * 0.9
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

        LaunchedEffect(focusRequester) {
            focusRequester.requestFocus()
        }
    }

}
