package com.lq.joy.ui.page.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lq.joy.data.AppContainer
import com.lq.joy.ui.page.common.CenterLoadingContent

@Composable
fun DetailScreen(
    appContainer: AppContainer,
    url: String
) {
    val viewModel: DetailViewModel =
        viewModel(factory = DetailViewModel.providerFactory(appContainer.sakuraRepository))


    val uiState by viewModel.uiState.collectAsState()

    CenterLoadingContent(
        isLoading = uiState.isLoading,
        isEmpty = when (uiState) {
            is DetailUiState.HasData -> false
            is DetailUiState.NoData -> true
        },
        modifier = Modifier.fillMaxSize(),
        contentEmpty = {
            Text(text = "无数据", modifier = Modifier.align(Alignment.Center))
        }) {
        //https://stackoverflow.com/questions/69558033/kotlin-error-smart-cast-to-x-is-impossible-because-state-is-a-property-that
        val _uiState = uiState
        check(_uiState is DetailUiState.HasData)

        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(_uiState.data.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = _uiState.data.animationName,
                /*modifier = Modifier
                    .width(imageWidth.dp)
                    .height(imageHeight.dp),*/
                contentScale = ContentScale.FillBounds
            )
        }
    }

}