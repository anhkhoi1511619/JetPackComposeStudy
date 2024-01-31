package com.example.jetpackcomposeexample.view.vico.article

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
import com.example.jetpackcomposeexample.model.helper.dto.Post
import com.example.jetpackcomposeexample.model.helper.dto.impl.post3
import com.example.jetpackcomposeexample.model.helper.history.HistoryDataModel
import com.example.jetpackcomposeexample.model.helper.history.PostHistoryData
import com.example.jetpackcomposeexample.view.vico.utils.BookmarkButton


@Composable
fun PostCardSimple(
    data: PostHistoryData,
    isFavorite: Boolean
) {
    Row {
        PostImage(post = post3, modifier = Modifier.padding(16.dp))
        Column (
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp)
        ) {
            PostCardTitle(data = data)
            AuthorAndReadTime(data = data)
        }
        BookmarkButton(isBookmarked = isFavorite, onClick = { /*TODO*/ })
    }
}
@Composable
fun AuthorAndReadTime(
    data: PostHistoryData,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(
            id = R.string.home_post_min_read,
            formatArgs = arrayOf(
                data.author,
                data.date
            )
        ),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun PostCardTitle(data: PostHistoryData, ){
    Text(
        text = data.title,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun PostImage(post: Post, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = post.imageThumbId),
        contentDescription = null,
        modifier = modifier
            .size(40.dp, 40.dp)
            .clip(MaterialTheme.shapes.small)
    )
}

@Preview
@Composable
fun PostCardSimpleTest() {
    PostCardSimple(data = HistoryDataModel.model1, isFavorite = false)
}

@Preview
@Composable
fun PostImageTest() {
    PostImage(post = post3)
}

@Preview
@Composable
fun PostCardTitleTest() {
    PostCardTitle(data = HistoryDataModel.model1)
}
@Preview
@Composable
fun AuthorAndReadTimeTest(){
    AuthorAndReadTime(data = HistoryDataModel.model1)
}