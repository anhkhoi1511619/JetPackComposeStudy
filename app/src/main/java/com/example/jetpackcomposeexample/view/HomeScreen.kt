package com.example.jetpackcomposeexample.view

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.R
import com.example.jetpackcomposeexample.model.post.dto.Post
import com.example.jetpackcomposeexample.model.post.post3
import com.example.jetpackcomposeexample.view.chart.ChartCode
import com.example.jetpackcomposeexample.view.viewmodel.UIViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcomposeexample.model.balance.BalanceResponse
import com.example.jetpackcomposeexample.model.experience.Experiences
import com.example.jetpackcomposeexample.model.experience.experiencesExampleList
import com.example.jetpackcomposeexample.model.history.HistoryDataModel
import com.example.jetpackcomposeexample.model.history.PostHistoryData
import com.example.jetpackcomposeexample.view.article.ArticleScreen
import com.example.jetpackcomposeexample.view.article.PostCardExperienceWorking
import com.example.jetpackcomposeexample.view.article.PostCardSimple
import com.example.jetpackcomposeexample.view.article.PostCardTop
import com.example.jetpackcomposeexample.view.article.Search
import com.example.jetpackcomposeexample.view.viewmodel.ScreenID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(uiViewModel: UIViewModel){
    val postUiState by uiViewModel.uiState.collectAsState()
    when(postUiState.screenID) {
        ScreenID.HOME -> {
            uiViewModel.update()
            PostList(
                detailPost = postUiState.loadedDetailPost,
                historyPosts = postUiState.historyPost,
                posts = postUiState.showingPostList,
                balanceList = postUiState.balanceList,
                favorites = emptySet(),
                onArticleTapped = {
                    uiViewModel.load(it)
                }
            )
        }
        ScreenID.DETAIL_POST -> {
            ArticleScreen(
                post = postUiState.loadedDetailPost,
                isExpandedScreen = false,
                onBack = { uiViewModel.backHome() },
                isFavorite = false,
                onToggleFavorite = { /*TODO*/ })
        }

        ScreenID.SETTING -> {
            /*TODO*/
        }

        else -> {
            /*TODO*/
        }
    }
}
var balanceListTest = arrayListOf(
    BalanceResponse.SFInfo(1.5f.toString(), "June 20", "12:00:00"),
    BalanceResponse.SFInfo(2.3f.toString(), "June 21", "12:00:00"),
    BalanceResponse.SFInfo(1.8f.toString(), "June 22", "12:00:00"),
    BalanceResponse.SFInfo(3.0f.toString(), "June 23", "12:00:00"),
    BalanceResponse.SFInfo(2.6f.toString(), "June 24", "12:00:00")
)
@Composable
fun PostList(
    detailPost: Post,
    historyPosts: List<PostHistoryData>,
//    posts: List<Post>,
    posts: List<Experiences>,
    balanceList: ArrayList<BalanceResponse.SFInfo>,
    favorites: Set<String>,
    onArticleTapped: (postId: Int) -> Unit,
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
            ExperienceWorking(posts = posts, navigateToArticle = onArticleTapped)
//            PostSocialActivities(posts = posts, navigateToArticle = onArticleTapped)
            ChartCode(balanceList, modifier = Modifier.padding(16.dp))
            PostListHistory(historyPosts = historyPosts, navigateToArticle = onArticleTapped, favorites = favorites)
        }
    }
}

@Composable
fun PostListHistory(
    historyPosts: List<PostHistoryData>,
    navigateToArticle: (Int) -> Unit,
    favorites: Set<String>
) {
    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_history_title) ,
            style = MaterialTheme.typography.titleLarge
        )
        historyPosts.forEach { list ->
            PostCardSimple(data = list, isFavorite =favorites.contains(""))
            PostListDivider()
        }
    }
}
@Composable
fun ExperienceWorking(
    posts: List<Experiences>,
    navigateToArticle: (Int) -> Unit
) {
    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_experience_working_title) ,
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
                PostCardExperienceWorking(experiences = x, navigateToArticle =navigateToArticle, modifier =Modifier)
            }
            Spacer(Modifier.height(16.dp))
            PostListDivider()
        }
    }
}

//@Composable
//fun ExperienceWorking(
//    posts: List<Post>,
//    navigateToArticle: (String) -> Unit
//) {
//    Column {
//        Text(
//            modifier = Modifier.padding(16.dp),
//            text = stringResource(id = R.string.home_social_activities_title) ,
//            style = MaterialTheme.typography.titleLarge
//        )
//        Row (
//            modifier = Modifier
//                .horizontalScroll(rememberScrollState())
//                .height(IntrinsicSize.Max)
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            posts.forEach { x ->
//                PostCardPopular(post = x, navigateToArticle =navigateToArticle, modifier =Modifier)
//            }
//            Spacer(Modifier.height(16.dp))
//            PostListDivider()
//        }
//    }
//}

@Composable
fun PostTopSection(post: Post, navigateToArticle: (Int)->Unit) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
        text = stringResource(id = R.string.home_top_section_title),
        style = MaterialTheme.typography.titleMedium
    )
    PostCardTop(post = post, modifier = Modifier.clickable { navigateToArticle(1) })
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
    PostList(post3, HistoryDataModel.list, experiencesExampleList,ArrayList(), emptySet(), {})
}
@Preview
@Composable
fun PostListPopularTest() {
    ExperienceWorking(experiencesExampleList, navigateToArticle = {})
}
@Preview
@Composable
fun HomeScreenTest() {
    HomeScreen(uiViewModel = viewModel())
}