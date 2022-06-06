package com.lq.joy.ui.page.search

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lq.joy.R
import com.lq.joy.TAG
import com.lq.joy.data.Api
import com.lq.joy.data.ui.SearchBean
import com.lq.joy.ui.page.common.FullScreenLoading
import com.lq.joy.ui.page.common.ItemRow
import com.lq.joy.utils.Keyboard
import com.lq.joy.utils.keyboardAsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    var isInitUi by remember {
        mutableStateOf(true)
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    SearchBoxWithContent(uiState, focusManager = focusManager, resultContent = {
        viewModel.searchFlow?.run {
            val data = collectAsLazyPagingItems()
            if (data.itemCount > 0) {
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
                                    Spacer(modifier = Modifier.padding(5.dp))
                                    ItemRow(item = item, modifier = Modifier.padding(start = 5.dp))
                                    Spacer(modifier = Modifier.padding(5.dp))
                                    Divider(thickness = Dp.Hairline)
                                }
                            }
                        }
                    }
                }
            } else {
                FullScreenLoading()
            }
        }
    }, onSearch = {
        viewModel.search(it)
        isInitUi = false
        keyboardController?.hide()
    }, isInitUi = isInitUi)
}

@Composable
fun SearchBoxWithContent(
    uiState: SearchViewModelState,
    resultContent: @Composable() (BoxScope.() -> Unit),
    onSearch: (String) -> Unit,
    isInitUi: Boolean,
    focusManager: FocusManager
) {
    var searchInCenter by remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                Log.d(TAG, "parent:${it.size}")
            }
    ) {

        AnimatedVisibility(
            visible = !searchInCenter,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = (64 + 10).dp)
            ) {
                resultContent()
            }
        }

        SearchBox(
            searchInCenter = searchInCenter,
            focusManager = focusManager,
            onFocusGet = {
                Log.d(TAG, "set searchInCenter:${!it}")
                if (isInitUi) {
                    searchInCenter = !it
                }

            },
            onSearch = onSearch
        )
    }

}

@Composable
private fun SearchBox(
    searchInCenter: Boolean,
    focusManager: FocusManager,
    onFocusGet: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        var searchContent by remember { mutableStateOf("") }
        val y = remember {
            Animatable(0f)
        }

        val scope = rememberCoroutineScope()
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val width = screenWidth * 0.8

        var distance by remember {
            mutableStateOf(-1f)
        }
        with(LocalDensity.current) {
            val statusBarHeight = WindowInsets.statusBars.getTop(LocalDensity.current)
            OutlinedTextField(
                value = searchContent,
                onValueChange = { searchContent = it },
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(width.dp)
                    .height(64.dp)
                    .offset(y = y.value.toDp())
                    .onFocusChanged {
                        onFocusGet(it.isFocused)
                    }
                    .onGloballyPositioned { coordinates ->
                        if (distance == -1f) {

                            val positionInParent = coordinates.positionInWindow()
                            Log.d(
                                TAG,
                                "search box position:$positionInParent height:${coordinates.size.height}"
                            )
                            distance = positionInParent.y - 10.dp.toPx() - statusBarHeight
                        }
                    },
                shape = RoundedCornerShape(18.dp),
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSearch(searchContent)
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
        }

        LaunchedEffect(key1 = searchInCenter) {
            scope.launch {
                if (!searchInCenter) {
                    if (y.value == 0f) {
                        //向上
                        y.animateTo(-distance, animationSpec = tween(300))
                    }
                } else {
                    if (y.value == -distance) {
                        //向下
                        y.animateTo(0f, animationSpec = tween(300))
                    }

                }
            }
        }

        val keyboardAsState = keyboardAsState()
        LaunchedEffect(key1 = keyboardAsState.value) {
            scope.launch {
                when (keyboardAsState.value) {
                    Keyboard.Closed -> {
                        Log.d(TAG, "Keyboard.Closed,searchInCenter:${searchInCenter}")
                        if (!searchInCenter) focusManager.clearFocus()
                    }
                }
            }
        }
    }
}