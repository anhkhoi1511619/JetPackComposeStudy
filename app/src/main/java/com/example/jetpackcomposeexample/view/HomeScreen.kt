package com.example.jetpackcomposeexample.view

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.compose.jetchat.components.ChannelNameBar
import com.example.jetpackcomposeexample.model.balance.SubtractBalanceResponse
import com.example.jetpackcomposeexample.model.card.TransitHistory
import com.example.jetpackcomposeexample.model.experience.Experiences
import com.example.jetpackcomposeexample.model.experience.experiencesExampleList
import com.example.jetpackcomposeexample.utils.TLog
import com.example.jetpackcomposeexample.utils.TLog_Sync
import com.example.jetpackcomposeexample.view.article.ArticleScreen
import com.example.jetpackcomposeexample.view.article.BalanceAddForm
import com.example.jetpackcomposeexample.view.article.BalanceCardSimple
import com.example.jetpackcomposeexample.view.article.BalanceHistoryHeader
import com.example.jetpackcomposeexample.view.article.PostCardExperienceWorking
import com.example.jetpackcomposeexample.view.article.PostCardTop
import com.example.jetpackcomposeexample.view.article.TransitCardSimple
import com.example.jetpackcomposeexample.view.utils.formatValues
import com.example.jetpackcomposeexample.view.viewmodel.ScreenID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(uiViewModel: UIViewModel){
    val postUiState by uiViewModel.uiState.collectAsState()
    when(postUiState.screenID) {
        ScreenID.HOME -> {
//            uiViewModel.update()
            PostList(
                detailPost = postUiState.loadedDetailPost,
                transitHistoryList = postUiState.historyTransitList,
//                historyPosts = postUiState.historyPost,
                posts = postUiState.showingPostList,
                balanceList = postUiState.balanceList,
                favorites = emptySet(),
                onArticleTapped = {
                    uiViewModel.load(it)
                },
                onSubtractConfirm = { money, date, time ->
                    // Khi nhấn Xác nhận
                    Log.d("ADD", "Khi nhấn Subtract money "+money+" date "+date+" time "+time)
                    val (formattedMoney, formattedDate, formattedTime) = formatValues(money, date, time)
                    TLog_Sync.d("ADD", "Khi nhấn Subtract money "+formattedMoney+" date "+formattedDate+" time "+formattedTime)
                    uiViewModel.subtractBalance(formattedMoney, formattedDate, formattedTime)
                },
                onAddConfirm = { money, date, time ->
                    // Khi nhấn Xác nhận
                    Log.d("ADD", "Khi nhấn Add money "+money+" date "+date+" time "+time)
                    val (formattedMoney, formattedDate, formattedTime) = formatValues(money, date, time)
                    TLog_Sync.d("ADD", "Khi nhấn Add money "+formattedMoney+" date "+formattedDate+" time "+formattedTime)
                    uiViewModel.addBalance(formattedMoney, formattedDate, formattedTime)
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostList(
    detailPost: Post,
    transitHistoryList: List<TransitHistory>,
    posts: List<Experiences>,
    balanceList: ArrayList<SubtractBalanceResponse.SFInfo>,
    favorites: Set<String>,
    onArticleTapped: (postId: Int) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onSubtractConfirm: (String, String, String) -> Unit, // money, date, time
    onAddConfirm: (String, String, String) -> Unit, // money, date, time
    state: LazyListState = rememberLazyListState(),
    ) {

    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    Scaffold(
        topBar = {
            ChannelNameBar(
                channelName = "Detail Profile",
                channelMembers = 1,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier,
            contentPadding = paddingValues,
            state = state
        ) {
            item {
//                Search(modifier = Modifier.padding(horizontal = 16.dp), onSearchInputChanged = {})
                PostTopSection(post = detailPost  , onArticleTapped)
                ExperienceWorking(posts = posts, navigateToArticle = onArticleTapped)
//            PostSocialActivities(posts = posts, navigateToArticle = onArticleTapped)
                ChartCode(balanceList, modifier = Modifier.padding(16.dp))
                BalanceListHistory(balanceList, onSubtractConfirm = onSubtractConfirm, onAddConfirm = onAddConfirm)
                TransitListHistory(transitHistoryList)
                //PostListHistory(historyPosts = historyPosts, navigateToArticle = onArticleTapped, favorites = favorites)
            }
        }
    }

}

@Composable
fun TransitListHistory(
    historyTransits: List<TransitHistory>,
) {
    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.transit_history_title) ,
            style = MaterialTheme.typography.titleLarge
        )
        historyTransits.forEach { list ->
            TransitCardSimple(data = list)
            PostListDivider()
        }
    }
}

@Composable
fun BalanceListHistory(
    historyTransits: List<SubtractBalanceResponse.SFInfo>,
    onSubtractConfirm: (String, String, String) -> Unit, // money, date, time
    onAddConfirm: (String, String, String) -> Unit // money, date, time
) {
    var showAddForm by remember { mutableStateOf(false) }
    var moneyInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("") }
    var timeInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        BalanceHistoryHeader(
            title = stringResource(id = R.string.balance_history_title),
            onAddClick = { showAddForm = !showAddForm }
        )

        if (showAddForm) {
            BalanceAddForm(
                money = moneyInput,
                date = dateInput,
                time = timeInput,
                onMoneyChange = { moneyInput = it },
                onDateChange = { dateInput = it },
                onTimeChange = { timeInput = it },
                onSubtract = {
                    onSubtractConfirm(moneyInput, dateInput, timeInput)
                    showAddForm = false
                    moneyInput = ""
                    dateInput = ""
                    timeInput = ""
                },
                onAdd = {
                    onAddConfirm(moneyInput, dateInput, timeInput)
                    showAddForm = false
                    moneyInput = ""
                    dateInput = ""
                    timeInput = ""
                }
            )
        }

        historyTransits
            .takeLast(10)
            .forEach { list ->
            BalanceCardSimple(data = list)
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
//    PostList(post3, transitHistoryList, experiencesExampleList,ArrayList(), emptySet(), {}, {})
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