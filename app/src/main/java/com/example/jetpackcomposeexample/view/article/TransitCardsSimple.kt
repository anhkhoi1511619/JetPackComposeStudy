package com.example.jetpackcomposeexample.view.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.R
import com.example.jetpackcomposeexample.model.card.TransitHistory
import com.example.jetpackcomposeexample.model.post.dto.Post
import com.example.jetpackcomposeexample.model.post.post3
import com.example.jetpackcomposeexample.model.history.HistoryDataModel
import com.example.jetpackcomposeexample.model.history.PostHistoryData
import com.example.jetpackcomposeexample.view.utils.BookmarkButton
import java.text.DateFormat


@Composable
fun TransitCardSimple(
    data: TransitHistory,
) {
    Row {
        TransitImage(modifier = Modifier.padding(16.dp))
        Column (
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp)
        ) {
            TransitCardTitle(data = data)
            TransitAuthorAndReadTime(data = data)
        }
        BookmarkButton(isBookmarked = false, onClick = { /*TODO*/ })
    }
}
@Composable
fun TransitAuthorAndReadTime(
    data: TransitHistory,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(
            id = R.string.transit_history,
            formatArgs = arrayOf(
                data.date,
                data.balance
            )
        ),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun TransitCardTitle(data: TransitHistory ){
    Text(
        text = "Station "+data.stationCode +" Line "+data.lineCode,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun TransitImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = post3.imageThumbId),
        contentDescription = null,
        modifier = modifier
            .size(40.dp, 40.dp)
            .clip(MaterialTheme.shapes.small)
    )
}

@Preview
@Composable
fun TransitCardSimpleTest() {
    TransitCardSimple(data = TransitHistory("2025-06-15", 25, 1, 251, 578))
}

@Preview
@Composable
fun TransitImageTest() {
    TransitImage()
}

@Preview
@Composable
fun TransitCardTitleTest() {
    TransitCardTitle(data = TransitHistory("2025-06-15", 25, 1, 251, 578))
}
@Preview
@Composable
fun TransitAuthorAndReadTimeTest(){
    TransitAuthorAndReadTime(data = TransitHistory("2025-06-15", 25, 1, 251, 578))
}