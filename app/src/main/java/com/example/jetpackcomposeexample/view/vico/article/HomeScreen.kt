package com.example.jetpackcomposeexample.view.vico.article

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.R
import com.example.jetpackcomposeexample.model.helper.dto.Post
import com.example.jetpackcomposeexample.model.helper.dto.impl.post3
import com.example.jetpackcomposeexample.model.helper.dto.impl.posts
import com.example.jetpackcomposeexample.view.vico.chart.ChartCode
import com.example.jetpackcomposeexample.view.vico.viewModel.PostViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(postViewModel: PostViewModel =  viewModel()){
    val postUiState by postViewModel.uiState.collectAsState()
    // Create a CoroutineScope that follows this composable's lifecycle
//    val composableScope = rememberCoroutineScope()
    if(postUiState.isClicking) {
        ArticleScreen(
            post = postUiState.loadedDetailPost,
            isExpandedScreen = false,
            onBack = { postViewModel.backHome() },
            isFavorite = false,
            onToggleFavorite = { /*TODO*/ })
    } else {
        PostList(
            detailPost = postUiState.loadedDetailPost,
            posts = postUiState.showingPostList,
            favorites = emptySet(),
            onArticleTapped = {
                postViewModel.load(it)
            }
        )

    }
}
@Composable
fun PostList(
    detailPost: Post,
    posts: List<Post>,
    favorites: Set<String>,
    onArticleTapped: (postId: String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    ) {
    LazyColumn(
        modifier = Modifier,
        contentPadding = contentPadding,
        state = state
        ) {
        item {
            Search(modifier = Modifier.padding(horizontal = 16.dp), onSearchInputChanged = {})
            PostTopSection(post = detailPost  , onArticleTapped)
            PostListSimpleSection(posts = posts, navigateToArticle = onArticleTapped, favorites = favorites)
            ChartCode(modifier = Modifier.padding(16.dp))
            PostListPopular(posts = posts, navigateToArticle = onArticleTapped)
        }
    }
}

@Composable
fun PostListSimpleSection(
    posts: List<Post>,
    navigateToArticle: (String) -> Unit,
    favorites: Set<String>
) {
    Column {
        posts.forEach { list ->
            PostCardSimple(post = list, isFavorite =favorites.contains(list.id))
            PostListDivider()
        }
    }
}

@Composable
fun PostListPopular(
    posts: List<Post>,
    navigateToArticle: (String) -> Unit
) {
    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_popular_section_title) ,
            style = MaterialTheme.typography.titleLarge
        )
        Row (
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .height(IntrinsicSize.Max)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            posts.forEach { x ->
                PostCardPopular(post = x, navigateToArticle =navigateToArticle, modifier =Modifier)
            }
            Spacer(Modifier.height(16.dp))
            PostListDivider()
        }
    }
}

@Composable
fun PostTopSection(post: Post, navigateToArticle: (String)->Unit) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
        text = stringResource(id = R.string.home_top_section_title),
        style = MaterialTheme.typography.titleMedium
    )
    PostCardTop(post = post, modifier = Modifier.clickable { navigateToArticle("1") })
    PostListDivider()
}

@Composable
fun PostListDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    )
}


@Preview
@Composable
fun PostListDividerTest() {
    PostListDivider()
}
@Preview
@Composable
fun PostListToSectionTest(){
    PostTopSection(post = post3, navigateToArticle = {})
}
@Preview
@Composable
fun PostListTest() {
    PostList(post3, posts, emptySet(), {})
}
@Preview
@Composable
fun PostListPopularTest() {
    PostListPopular(posts, navigateToArticle = {})
}
@Preview
@Composable
fun HomeScreenTest() {
    HomeScreen()
}