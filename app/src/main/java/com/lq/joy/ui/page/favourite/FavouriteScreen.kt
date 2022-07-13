package com.lq.joy.ui.page.favourite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lq.joy.db.Favourite

@Composable
fun FavouriteScreen(viewModel: FavouriteViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val paging = uiState.pagingData.collectAsLazyPagingItems()

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        Text(text = "收藏")
    }) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(paging) { item ->
                item?.let {
                    FavouriteItem(favourite = it)
                }
            }

        }
    }
}

@Composable
fun FavouriteItem(favourite: Favourite, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(favourite.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = favourite.name,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
        Text(text = favourite.name, color = MaterialTheme.colors.onBackground.copy(0.5f))
    }


}