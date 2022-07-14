package com.lq.joy.ui.page.favourite

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lq.joy.TAG
import com.lq.joy.data.SourceType
import com.lq.joy.db.Favourite

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FavouriteScreen(viewModel: FavouriteViewModel, finish: () -> Unit, jumpDetail: (Int, String) -> Unit) {

    val uiState by viewModel.uiState.collectAsState()
    val paging = uiState.pagingData.collectAsLazyPagingItems()

    var deleteDialogShow by remember {
        mutableStateOf(false)
    }

    var longClickIndex by remember {
        mutableStateOf(-1)
    }


    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        Surface(shadowElevation = 5.dp) {
            CenterAlignedTopAppBar(title = {
                Text(text = "收藏", color = MaterialTheme.colorScheme.onBackground)
            }, navigationIcon = {
                IconButton(onClick = finish) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                }
            })
        }

    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 5.dp)
        ) {
            itemsIndexed(paging, key = { index, fav -> fav.uniqueTag }) { index, item ->
                item?.let {
                    Column {
                        FavouriteItem(
                            favourite = it, modifier = Modifier
                                .combinedClickable(
                                    onClick = {
                                        jumpDetail(it.type, it.jumpKey)
                                    },
                                    onLongClick = {
                                        longClickIndex = index
                                        Log.d(TAG, "longClick${longClickIndex}")
                                        deleteDialogShow = true
                                    },
                                )
                                .padding(5.dp)
                                .fillMaxWidth()
                        )
                        Divider(
                            thickness = Dp.Hairline,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                        )
                    }
                }
            }
        }

        if (deleteDialogShow && longClickIndex != -1) {
            AlertDialog(onDismissRequest = {
                deleteDialogShow = false
                longClickIndex = -1
            }, confirmButton = {

                IconButton(onClick = {
                    paging[longClickIndex]?.let {
                        viewModel.deleteFavourite(it)
                    }
                    deleteDialogShow = false
                    longClickIndex = -1
                }) {
                    Icon(imageVector = Icons.Rounded.Done, contentDescription = "")
                }
            }, dismissButton = {
                IconButton(onClick = {
                    deleteDialogShow = false
                    longClickIndex = -1
                }) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "")
                }
            }, text = {
                Text(text = "取消收藏?", textAlign = TextAlign.Center, fontSize = 16.sp)
            })
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
                .aspectRatio(4f / 4f)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(start = 5.dp)) {
            Text(
                text = favourite.name,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp
            )

            Text(
                text = "来源：${
                    when (favourite.type) {
                        SourceType.SAKURA.ordinal -> SourceType.SAKURA.netName
                        SourceType.NAIFEI.ordinal -> SourceType.NAIFEI.netName
                        else -> ""
                    }
                }",
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 5.dp)
            )

        }
    }


}