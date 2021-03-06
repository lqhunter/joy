package com.lq.joy.ui.page.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lq.joy.R
import com.lq.joy.TAG
import com.lq.joy.data.Api
import com.lq.joy.data.sakura.bean.HomeItemBean
import com.lq.joy.data.ui.VideoSearchBean
import com.lq.joy.ui.theme.Grey500

@Composable
fun FullScreenLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
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
                Text(text = "?????????")
                Text(text = item.newestEpisode ?: "???", color = Color.Red)
            }
            var tag = ""
            item.tags?.let {
                it.forEach { t ->
                    tag += "${t.name} "
                }
            }
            Text(text = "?????????$tag")
        }


    }
}

@Composable
fun ItemRow(
    item: VideoSearchBean.SakuraBean,
    modifier: Modifier = Modifier,
    onClick: ((String) -> Unit)? = null
) {
    if (onClick != null) {
        modifier.clickable {
            onClick(item.detailUrl)
        }
    }
    Row(
        modifier = modifier
            .height(100.dp)
            .padding(top = 10.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically
    )
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
                Text(text = "?????????")
                Text(text = item.newestEpisode ?: "???", color = Color.Red)
            }
            var tag = ""
            item.tags?.let {
                it.forEach { t ->
                    tag += "${t.name} "
                }
            }
            Text(text = "?????????$tag")
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
                text = "?????????${item.newestEpisode ?: "??????"}",
                color = MaterialTheme.colors.onBackground.copy(0.5f),
                fontSize = 14.sp,
                maxLines = 1,
                modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
            )


        }
    }
}

@Composable
fun ItemRow(item: VideoSearchBean.NaifeiBean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(top = 10.dp, bottom = 10.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Api.NAIFEI_HOST + "/" + item.coverUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .fallback(R.drawable.placeholder)
                .crossfade(true)
                .build(),
            contentDescription = item.name,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(3f / 4f)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = item.name,
                color = MaterialTheme.colors.onBackground,
                fontSize = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "?????????${item.area}",
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
            )
            Text(
                text = "?????????${item.type}",
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
            )
            Row {
                val remark = item.remarks
                if (remark.contains("???") || remark.contains("??????")) {
                    Text(text = remark, color = Color.Red, modifier = Modifier.padding(end = 5.dp))
                }
            }
        }

        VerticalDivider(
            thickness = Dp.Hairline,
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
        )

        Column(
            modifier = modifier
                .fillMaxHeight()
                .width(80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "????????????", color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f))

            Text(text = item.score, fontSize = 25.sp)
        }
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp
) {
    val indentMod = if (startIndent.value != 0f) {
        Modifier.padding(start = startIndent)
    } else {
        Modifier
    }
    val targetThickness = if (thickness == Dp.Hairline) {
        (1f / LocalDensity.current.density).dp
    } else {
        thickness
    }
    Box(
        modifier
            .then(indentMod)
            .fillMaxHeight()
            .width(targetThickness)
            .background(color = color)
    )
}


fun drawTicketPath(
    size: Size,
    lineWidth: Float,
    LineFirst: Boolean = false,
    LineEnd: Boolean = false
): Path {
    val halfLineWidth = lineWidth / 2
    return Path().apply {
        reset()
        // Top left arc
        if (!LineFirst) {
            arcTo(
                rect = Rect(
                    left = -(size.height / 3) + halfLineWidth,
                    top = 0f + halfLineWidth,
                    right = size.height / 3 - halfLineWidth,
                    bottom = size.height - halfLineWidth
                ),
                startAngleDegrees = 90.0f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
        } else {
            moveTo(halfLineWidth, size.height)
            lineTo(x = halfLineWidth, y = halfLineWidth)
        }

        if (!LineEnd) {
            lineTo(x = size.width - (size.height / 3), y = halfLineWidth)
            // Top right arc
            arcTo(
                rect = Rect(
                    left = size.width - (size.height / 3) * 2 + halfLineWidth,
                    top = 0f + halfLineWidth,
                    right = size.width - halfLineWidth,
                    bottom = size.height - halfLineWidth
                ),
                startAngleDegrees = -90.0f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
        } else {
            lineTo(x = size.width - halfLineWidth, y = halfLineWidth)
            lineTo(x = size.width - halfLineWidth, y = size.height - halfLineWidth)
        }

        close()
    }
}

@Composable
fun SourceSelectorItem(
    name: String,
    isSelected: Boolean,
    type: SourceUiType,
    modifier: Modifier = Modifier
) {
    val unSelectedColor = MaterialTheme.colors.secondaryVariant
    Box(
        modifier
            .drawBehind {
                drawPath(
                    drawTicketPath(size, 1.dp.toPx(), LineFirst = type == SourceUiType.FIRST, LineEnd = type == SourceUiType.END),
                    if (isSelected) unSelectedColor else Grey500,
                    style = Stroke(width = 1.dp.toPx())
                )
            }, contentAlignment = Alignment.Center) {
            
        Text(text = name, fontSize = 12.sp)
    }
}

enum class SourceUiType {
    FIRST, MID, END
}


@Preview
@Composable
fun PreviewRect() {
    with(LocalDensity.current) {
        Box(
            Modifier
                .width(100.dp)
                .height(70.dp)
                .drawBehind {
                    drawPath(
                        drawTicketPath(size, 2.dp.toPx(), LineFirst = true, LineEnd = false),
                        Color.Green,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }) {

        }
    }

}

//@Preview
@Composable
fun PreviewItem() {
    ItemRow(
        item = HomeItemBean(
            "5564",
            "???????????? ?????????",
            "http://css.yhdmtu.com/news/2022/04/12/20220412082843373.jpg",
            detailUrl = "/show/5564.html"
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

    }


}
