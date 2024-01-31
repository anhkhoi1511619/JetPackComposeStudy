package com.example.jetpackcomposeexample.view.vico.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.R
import com.example.jetpackcomposeexample.model.helper.dto.Post
import com.example.jetpackcomposeexample.model.helper.dto.impl.post3

@Composable
fun PostCardTop(post: Post, modifier: Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(post.imageId),
            contentDescription = null,
            modifier = Modifier
                .heightIn(180.dp)
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
            )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = post.title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
            )
        Text(
            text = post.metaData.author.name,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = stringResource(
                id = R.string.home_post_min_read,
                formatArgs = arrayOf(
                    post.metaData.date,
                    post.metaData.readTimeMinutes
                )
            ),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
fun PostCardTopTest() {
    PostCardTop(post = post3, modifier = Modifier)
}