package com.example.jetpackcomposeexample.view.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.R
import com.example.jetpackcomposeexample.model.post.Post
import com.example.jetpackcomposeexample.model.post.post3
import com.example.jetpackcomposeexample.view.theme.JetpackComposeExampleTheme
import com.example.jetpackcomposeexample.view.utils.BookmarkButton
import com.example.jetpackcomposeexample.view.utils.FavoriteButton
import com.example.jetpackcomposeexample.view.utils.ShareButton
import com.example.jetpackcomposeexample.view.utils.TextSettingButton

@ExperimentalMaterial3Api
@Composable
fun ArticleScreen(
    post: Post,
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {
    var showUnimplementedActionDialog by rememberSaveable { mutableStateOf(false) }
    if (showUnimplementedActionDialog) {
        FunctionalityNotAvailablePopup { showUnimplementedActionDialog = false }
    }
    Row(Modifier.fillMaxSize()) {
        val context = LocalContext.current
        ArticleScreenContent(
            post = post,
            navigationIconContent = {
                if (!isExpandedScreen) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_navigate_up),
                            tint = MaterialTheme.colorScheme.primary
                            )

                    }
                }
            },
            bottomBarContent = {
                if (!isExpandedScreen) {
                    BottomAppBar (
                        actions = {
                            FavoriteButton (onClick = {showUnimplementedActionDialog = true})
                            BookmarkButton(isBookmarked = isFavorite, onClick = onToggleFavorite)
                            ShareButton {}
                            TextSettingButton (onClick = {showUnimplementedActionDialog = true})
                        }
                    )
                }
            },
            lazyListState = lazyListState
            )
    }
}


@ExperimentalMaterial3Api
@Composable
private fun ArticleScreenContent(
    post: Post,
    navigationIconContent: @Composable ()->Unit = { },
    bottomBarContent: @Composable ()->Unit = { },
    lazyListState: LazyListState = rememberLazyListState()
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    Scaffold(
        topBar = {
            TopAppBar(
                title = post.publication?.name.orEmpty(),
                navigationIconContent = navigationIconContent,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = bottomBarContent
    ) {innerPadding->
        PostContent(post = post,
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(innerPadding),
        state = lazyListState
    )
    }
}

@ExperimentalMaterial3Api
@Composable
private fun TopAppBar (
    title: String,
    navigationIconContent: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(title = {
        Row {
            Image(painter = painterResource(id = R.drawable.icon_article_background),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(36.dp))
            Text(
                text = stringResource(id = R.string.published_in, title),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp)
                )
        }
    },
        navigationIcon = navigationIconContent,
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        text = {
            Text(text = stringResource(id = R.string.article_functionality_not_available),
                style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.close))
            }
        })
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun PreviewArticleContent() {
    JetpackComposeExampleTheme {
        ArticleScreenContent(post = post3,{},{})
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun PreviewArticleScreen() {
    JetpackComposeExampleTheme {
        ArticleScreen(
            post = post3,
            isExpandedScreen = false,
            onBack = { /*TODO*/ },
            isFavorite = false,
            onToggleFavorite = { /*TODO*/ })
    }
}