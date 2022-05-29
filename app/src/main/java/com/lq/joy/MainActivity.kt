package com.lq.joy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lq.joy.ui.home.HomeUiState
import com.lq.joy.ui.home.HomeViewModel
import com.lq.joy.ui.theme.JoyTheme
import com.lq.lib_sakura.Api

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as JoyApplication).container

        setContent {
            JoyTheme {
                val viewModel: HomeViewModel =
                    viewModel(factory = HomeViewModel.providerFactory(appContainer.sakuraRepository))

                val uiState by viewModel.uiState.collectAsState()

                when (uiState) {
                    is HomeUiState.HasData -> {
                        //todo 写一个banner

                        val groups = (uiState as HomeUiState.HasData).data.groups
                        LazyColumn {
                            groups.forEach { group ->
                                item {
                                    TextButton(onClick = { /*TODO*/ }) {
                                        Text(text = group.groupTitle)
                                    }
                                }
                                items(group.items) { item ->
                                    Column(
                                        Modifier
                                            .padding(8.dp)
                                            .clickable {

                                            }) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(item.coverUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = item.name
                                        )
                                        Text(text = item.name)
                                    }
                                }

                            }
                        }


                    }
                    is HomeUiState.NoData -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(text = "无数据", modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }


            }
        }
    }
}
