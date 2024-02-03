package com.example.jetpackcomposeexample.view.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.R
import com.example.jetpackcomposeexample.model.post.dto.MetaData
import com.example.jetpackcomposeexample.model.post.dto.Paragraph
import com.example.jetpackcomposeexample.model.post.dto.ParagraphType
import com.example.jetpackcomposeexample.model.post.dto.Post
import com.example.jetpackcomposeexample.model.post.post3
import com.example.jetpackcomposeexample.view.theme.JetpackComposeExampleTheme


private val defaultSpacerSize = 16.dp

@Composable
fun PostContent(
    post: Post,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
){
    LazyColumn(
        contentPadding = PaddingValues(defaultSpacerSize),
        modifier = modifier,
        state = state,
        ) {
        postContentItem(post)
    }
}

fun LazyListScope.postContentItem(post: Post) {
    item {
        PostHeaderImage(post = post)
        Spacer(Modifier.height(defaultSpacerSize))
        Text(post.title, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        if(post.subtitle != null){
            Text(post.subtitle, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(defaultSpacerSize))
        }
    }
    item { PostMetadata(post.metaData, Modifier.padding(bottom = 24.dp)) }
    items(post.paragraph) { Paragraph(paragraph = it) }
}

@Composable
private fun PostHeaderImage(post: Post) {
    val imageModifier = Modifier
        .heightIn(min = 180.dp)
        .fillMaxWidth()
        .clip(shape = MaterialTheme.shapes.medium)

    Image(
        painter = painterResource(post.imageId),
        contentDescription = null,
        modifier = imageModifier,
        contentScale = ContentScale.Crop
    )
}
@Composable
private fun PostMetadata(
    metadata: MetaData,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier.semantics(mergeDescendants = true) {}
    ){
    Image(
        imageVector = Icons.Filled.AccountCircle,
        contentDescription = null,
        modifier = Modifier.size(40.dp),
        colorFilter = ColorFilter.tint(LocalContentColor.current),
        contentScale = ContentScale.Fit
    )
    Spacer(Modifier.width(8.dp))
    Column {
        Text(
            text = metadata.author.name,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 4.dp)
            )
        Text(
            text = stringResource(
                id = R.string.article_post_min_read,
                formatArgs = arrayOf(
                    metadata.date,
                    metadata.readTimeMinutes
                )
            ),
            style = MaterialTheme.typography.bodySmall,
        )
    }

    }
}

@Composable
private fun Paragraph(paragraph: Paragraph) {

    Box(modifier = Modifier.padding(bottom = 24.dp)){
        when(paragraph.type) {
            ParagraphType.Header -> {
                Text(modifier = Modifier.padding(4.dp),
                    text = paragraph.text,
                    )
            } else ->Text (
            modifier = Modifier.padding(4.dp),
            text = paragraph.text,
            )
        }
    }
}

@Preview
@Composable
fun PreviewContentPost() {
    JetpackComposeExampleTheme {
        Surface {
            PostContent(post = post3)
        }
    }
}