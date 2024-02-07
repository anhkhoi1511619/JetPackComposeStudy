@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jetpackcomposeexample.view.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.R
import com.example.jetpackcomposeexample.model.post.dto.Post
import com.example.jetpackcomposeexample.model.post.post3

@Composable
fun PostCardPopular(
    post: Post,
    navigateToArticle: (String) -> Unit,
    modifier: Modifier
) {
    Card (
        onClick = {navigateToArticle(post.id)},
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.width(280.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = post.imageId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                )
            Column (modifier.padding(16.dp)){
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = post.metaData.author.name,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis
                    )
                Text(
                    text = stringResource(
                        id = R.string.article_post_min_read,
                        formatArgs = arrayOf(
                            post.metaData.date,
                            post.metaData.readTimeMinutes
                        )
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun PostCardPopularTest() {
    PostCardPopular(post = post3, navigateToArticle = {}, modifier = Modifier)
}