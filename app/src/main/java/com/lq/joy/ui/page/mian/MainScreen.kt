@file:OptIn(ExperimentalMaterialApi::class)

package com.lq.joy.ui.page.mian

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lq.joy.data.AppContainer

@Composable
fun MainScreen(appContainer: AppContainer, onSearchClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Logo(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp, bottom = 10.dp)
        )

        FakeSearchView(onClick = onSearchClick)


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
                IconButton(onClick = { /*TODO*/ }) {
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