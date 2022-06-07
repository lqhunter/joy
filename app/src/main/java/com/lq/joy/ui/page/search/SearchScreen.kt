package com.lq.joy.ui.page.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.lq.joy.TAG
import com.lq.joy.data.ui.SearchBean
import com.lq.joy.ui.page.common.FullScreenLoading
import com.lq.joy.ui.page.common.ItemRow
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel, onVideoSelected: (SearchBean) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchContent by remember {
        mutableStateOf(uiState.key)
    }


    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val width = screenWidth * 0.8
        OutlinedTextField(
            value = searchContent,
            onValueChange = { searchContent = it },
            modifier = Modifier
                .width(width.dp)
                .height(64.dp),
            shape = RoundedCornerShape(18.dp),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.search(searchContent)
                    keyboardController?.hide()
                },
                onGo = {
                    Log.d(TAG, "onGo")

                },
                onSearch = {
                    Log.d(TAG, "onSearch")

                },
                onSend = {
                    Log.d(TAG, "onSend")

                }
            )
        )

        if (uiState.reload) {
            val data = viewModel.searchFlow!!.collectAsLazyPagingItems()

            LazyColumn {
                items(data) { item ->
                    if (item != null) {
                        when (item) {
                            is SearchBean.Title -> {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = item.title)
                                }
                            }
                            is SearchBean.NaifeiBean -> {
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
                    }
                }

                data.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item { Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            } }
                        }
                        loadState.append is LoadState.Loading -> {
                            item { CircularProgressIndicator() }
                        }
                        loadState.refresh is LoadState.Error -> {
                            val e = data.loadState.refresh as LoadState.Error
                            item {

                            }
                        }
                        loadState.append is LoadState.Error -> {
                            val e = data.loadState.append as LoadState.Error
                            item {

                            }
                        }
                    }
                }
            }



        }
    }

}

@Composable
fun SearchBoxWithContent(
    uiState: SearchViewModelState,
    resultContent: @Composable() (BoxScope.() -> Unit),
    onSearch: (String) -> Unit,
    isInitUi: Boolean,
    focusManager: FocusManager
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = (64 + 10).dp)
        ) {
            resultContent()
        }


    }

}

@Composable
private fun SearchBox(
    onFocusGet: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        var searchContent by remember { mutableStateOf("") }


    }
}