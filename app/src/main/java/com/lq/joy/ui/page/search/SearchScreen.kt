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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lq.joy.TAG
import com.lq.joy.data.AppRepository
import com.lq.joy.data.SourceType
import com.lq.joy.data.ui.VideoSearchBean
import com.lq.joy.ui.page.common.ItemRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch



@OptIn(ExperimentalPagerApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNaifeiSelected: (Int) -> Unit,
    onSakuraSelected: (String) -> Unit,
    appRepository: AppRepository
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

//    val lazyListState = rememberLazyListState()
    var searchContent by remember {
        mutableStateOf(uiState.key)
    }
    val focusRequester = remember {
        FocusRequester()
    }
//    val isScroll by remember {
//        lazyListState.isScrolled
//    }
    val pagerState = rememberPagerState()

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {

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
                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
                onFilterConfirm = {
                    scope.launch {
                        appRepository.saveSearchFilter(it)
                    }
                },
                filter = uiState.filter
            )
        }


        val naifeiPaging = uiState.naifeiFlow.collectAsLazyPagingItems()
        val sakuraPaging = uiState.sakuraFlow.collectAsLazyPagingItems()


        if (uiState.filter.size > 1) {
            TabRow(
                backgroundColor = MaterialTheme.colors.surface,
                selectedTabIndex = pagerState.currentPage,
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
                uiState.filter.forEachIndexed { index, s ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = s,
                            style = TextStyle(color = MaterialTheme.colors.onBackground)
                        )
                    }
                }
            }
        }

        HorizontalPager(count = uiState.filter.size, state = pagerState) { page ->
            if (uiState.filter.toList()[page] == SourceType.SAKURA.netName) {
                if (sakuraPaging.loadState.refresh is LoadState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                LazyColumn(/*state = lazyListState, */modifier = Modifier.fillMaxSize()) {
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

                            }
                        }
                    }
                }

            } else if (uiState.filter.toList()[page] == SourceType.NAIFEI.netName) {
                if (naifeiPaging.loadState.refresh is LoadState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                LazyColumn(/*state = lazyListState, */modifier = Modifier.fillMaxSize()) {
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
    onSearch: (String) -> Unit,
    onFilterConfirm: (Set<String>) -> Unit,
    filter:Set<String>
) {
    val focusManager = LocalFocusManager.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val width = screenWidth * 0.9
    var openDialog by remember { mutableStateOf(false) }
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
                        IconButton(onClick = {
                            openDialog = openDialog.not()
                        }) {
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

    if (openDialog) {
        val selected = remember {
            mutableStateMapOf(SourceType.SAKURA to filter.contains(SourceType.SAKURA.netName), SourceType.NAIFEI to filter.contains(SourceType.NAIFEI.netName))
        }
        Dialog(onDismissRequest = { openDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.surface,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(150.dp)

                            .padding(start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "樱花动漫")
                        Checkbox(
                            checked = selected[SourceType.SAKURA] ?: false,
                            onCheckedChange = { selected[SourceType.SAKURA] = it })
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(150.dp)

                            .padding(start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "奈飞")
                        Checkbox(
                            checked = selected[SourceType.NAIFEI] ?: false,
                            onCheckedChange = { selected[SourceType.NAIFEI] = it })
                    }

                    Text(
                        text = "确定",
                        modifier = Modifier
                            .width(150.dp)
                            .padding(top = 10.dp, bottom = 10.dp)
                            .clickable {
                                openDialog = false
                                val result = mutableSetOf<String>()
                                if (selected[SourceType.SAKURA] == true) result.add(SourceType.SAKURA.netName)
                                if (selected[SourceType.NAIFEI] == true) result.add(SourceType.NAIFEI.netName)
                                onFilterConfirm(result)
                            },
                        color = MaterialTheme.colors.secondaryVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

}
