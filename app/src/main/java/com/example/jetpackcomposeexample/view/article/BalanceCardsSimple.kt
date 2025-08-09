package com.example.jetpackcomposeexample.view.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.R
import com.example.jetpackcomposeexample.model.balance.BalanceResponse
import com.example.jetpackcomposeexample.model.card.TransitHistory
import com.example.jetpackcomposeexample.model.post.dto.Post
import com.example.jetpackcomposeexample.model.post.post3
import com.example.jetpackcomposeexample.model.history.HistoryDataModel
import com.example.jetpackcomposeexample.model.history.PostHistoryData
import com.example.jetpackcomposeexample.view.utils.BookmarkButton
import java.text.DateFormat



@Composable
fun BalanceAddForm(
    money: String,
    date: String,
    time: String,
    onMoneyChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = money,
            onValueChange = onMoneyChange,
            label = { Text("Tiền cần thêm vào") },
            singleLine = true
        )
        OutlinedTextField(
            value = date,
            onValueChange = onDateChange,
            label = { Text("Ngày tháng năm") },
            singleLine = true
        )
        OutlinedTextField(
            value = time,
            onValueChange = onTimeChange,
            label = { Text("Giờ phút") },
            singleLine = true
        )
        Button(
            onClick = onConfirm,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Xác nhận")
        }
    }
}


@Composable
fun BalanceHistoryHeader(
    title: String,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Button(
            onClick = onAddClick,
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Text("+", style = MaterialTheme.typography.titleLarge)
        }
    }
}


@Composable
fun BalanceCardSimple(
    data: BalanceResponse.SFInfo,
) {
    Row {
        BalanceImage(modifier = Modifier.padding(16.dp))
        Column (
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp)
        ) {
            BalanceCardTitle(data = data)
            BalanceAuthorAndReadTime(data = data)
        }
        BookmarkButton(isBookmarked = false, onClick = { /*TODO*/ })
    }
}
@Composable
fun BalanceAuthorAndReadTime(
    data: BalanceResponse.SFInfo,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(
            id = R.string.balance_history,
            formatArgs = arrayOf(
                data.postSubtractBalance,
                data.subtractAmount
            )
        ),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun BalanceCardTitle(data: BalanceResponse.SFInfo ){
    Text(
        text = data.date + " - "+data.time,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun BalanceImage(modifier: Modifier = Modifier) {
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
fun BalanceCardSimpleTest() {
    TransitCardSimple(data = TransitHistory("2025-06-15", 25, 1, 251, 578))
}

@Preview
@Composable
fun BalancemageTest() {
    TransitImage()
}

@Preview
@Composable
fun BalanceCardTitleTest() {
    TransitCardTitle(data = TransitHistory("2025-06-15", 25, 1, 251, 578))
}
@Preview
@Composable
fun BalanceAuthorAndReadTimeTest(){
    TransitAuthorAndReadTime(data = TransitHistory("2025-06-15", 25, 1, 251, 578))
}