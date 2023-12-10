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
import com.example.jetpackcomposeexample.view.vico.utils.BookmarkButton


@Composable
fun PostCardSimple(
    post: Post,
    isFavorite: Boolean
) {
    Row {
        PostImage(post = post, modifier = Modifier.padding(16.dp))
        Column (
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp)
        ) {
            PostCardTitle(post = post)
            AuthorAndReadTime(post = post)
        }
        BookmarkButton(isBookmarked = isFavorite, onClick = { /*TODO*/ })
    }
}
@Composable
fun AuthorAndReadTime(
    post: Post,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(
            id = R.string.home_post_min_read,
            formatArgs = arrayOf(
                post.metaData.author.name,
                post.metaData.readTimeMinutes
            )
        ),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun PostCardTitle(post: Post){
    Text(
        text = post.title,
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
    PostCardSimple(post = post3, isFavorite = false)
}

@Preview
@Composable
fun PostImageTest() {
    PostImage(post = post3)
}

@Preview
@Composable
fun PostCardTitleTest() {
    PostCardTitle(post = post3)
}
@Preview
@Composable
fun AuthorAndReadTimeTest(){
    AuthorAndReadTime(post = post3)
}