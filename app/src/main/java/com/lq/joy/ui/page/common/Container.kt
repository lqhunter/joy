package com.lq.joy.ui.page.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun SwipeRefreshContent(
    isLoading: Boolean,
    isEmpty: Boolean,
    onRefresh: () -> Unit,
    contentEmpty: @Composable () -> Unit,
    contentHasData: @Composable () -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isLoading),
        onRefresh = onRefresh,
    ) {
        if (isEmpty) {
            contentEmpty()
        } else {
            contentHasData()
        }
    }
}

@Composable
fun CenterLoadingContent(
    isLoading: Boolean,
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
    contentEmpty: @Composable BoxScope.() -> Unit,
    contentHasData: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        if (isEmpty) {
            contentEmpty()
        } else {
            contentHasData()
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }


}