package com.lq.joy.ui.page.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lq.joy.data.sakura.bean.HomeItemBean
import com.lq.joy.ui.theme.Blue500

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ItemRow(item: HomeItemBean, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
    Row(modifier = modifier
        .clickable {
            onClick(item.detailUrl)
        }
        .height(100.dp)
        .padding(top = 10.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically)
    {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = item.name,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = item.name,
                color = MaterialTheme.colors.onSurface,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row {
                Text(text = "状态：")
                Text(text = item.newestEpisode ?: "无", color = Color.Red)
            }
            var tag = ""
            item.tags?.let {
                it.forEach { t ->
                    tag += "${t.name} "
                }
            }
            Text(text = "类型：$tag")
        }


    }
}

@Composable
fun ItemColumn(
    item: HomeItemBean,
    modifier: Modifier = Modifier,
    onClick: (HomeItemBean) -> Unit
) {
    Card(elevation = 1.dp, modifier = modifier
        .clickable {
            onClick(item)
        }
        .fillMaxWidth(), backgroundColor = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = item.name,
                color = MaterialTheme.colors.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
            )

            Text(
                text = "最新：${item.newestEpisode?:"全集"}",
                color = MaterialTheme.colors.onBackground.copy(0.5f),
                fontSize = 14.sp,
                maxLines = 1,
                modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
            )


        }
    }
}

@Preview
@Composable
fun PreviewItem() {
    ItemRow(
        item = HomeItemBean(
            "5564",
            "境界战机 第二季",
            "http://css.yhdmtu.com/news/2022/04/12/20220412082843373.jpg",
            detailUrl = "/show/5564.html"
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

    }


}
