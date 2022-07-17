@file:OptIn(ExperimentalMaterialApi::class)

package com.lq.joy.ui.page.mian

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lq.joy.JoyApplication.Companion.context
import com.lq.joy.TAG
import com.lq.joy.data.AppContainer
import com.lq.joy.ui.page.common.VerticalDivider

@Composable
fun MainScreen(
    appContainer: AppContainer,
    onSearchClick: () -> Unit,
    onFavouriteClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            Logo(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp, bottom = 10.dp)
            )

            FakeSearchView(onClick = onSearchClick)
        }


        Surface(
            elevation = 10.dp, modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp, bottom = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f)
                        .clickable {
                            Log.d(TAG, "onFavouriteClick")
                            onFavouriteClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.padding(top = 3.dp, bottom = 3.dp)) {

                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = "收藏",
                            tint = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "收藏",
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }

//                VerticalDivider()
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.padding(top = 3.dp, bottom = 3.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "设置",
                            tint = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "设置",
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

}


@Composable
fun Logo(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = "JOY", fontSize = 45.sp)
    }
}

@Composable
fun FakeSearchView(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        elevation = 5.dp, shape = RoundedCornerShape(25.dp)
    ) {
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
                Text(text = "搜索...")
            }

            Row(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {
                    Toast.makeText(context, "假按钮，为了好看", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Mic,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                    )
                }
            }


        }
    }
}