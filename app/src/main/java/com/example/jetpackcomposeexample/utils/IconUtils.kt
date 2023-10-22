package com.example.jetpackcomposeexample.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetpackcomposeexample.R
import com.example.jetpackcomposeexample.ui.theme.JetpackComposeExampleTheme


@Composable
fun FavoriteButton(onClick: ()-> Unit) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Filled.ThumbUp,
            contentDescription = stringResource(R.string.cd_add_to_favorites)
        )
    }
}

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clickLabel = stringResource(
        if (isBookmarked) R.string.unbookmark else R.string.bookmark
    )

    IconToggleButton(checked = isBookmarked,
        onCheckedChange = { onClick()},
        modifier = modifier.semantics {
            this.onClick ( label = clickLabel, action = null )
        }
        ) {
            Icon(imageVector = if (isBookmarked) Icons.Filled.Notifications else Icons.Filled.LocationOn,
        contentDescription = null)
    }
}

@Composable
fun ShareButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(imageVector = Icons.Filled.Share,
            contentDescription = stringResource(id = R.string.cd_share))
    }
}

@Composable
fun TextSettingButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(painter = painterResource(R.drawable.ic_text_settings),
            contentDescription = stringResource(id = R.string.cd_text_settings))
    }
}

@Preview
@Composable
fun BookmarkButtonPreview() {
    JetpackComposeExampleTheme {
        Surface {
            BookmarkButton(isBookmarked = false, onClick = {})
        }
    }
}
@Preview
@Composable
fun FavoriteButtonPreview() {
    JetpackComposeExampleTheme {
        Surface {
            FavoriteButton {}
        }
    }
}
@Preview
@Composable
fun ShareButtonPreview() {
    JetpackComposeExampleTheme {
        Surface {
            ShareButton {

            }
        }
    }
}

@Preview
@Composable
fun TextSettingButtonPreview() {
    JetpackComposeExampleTheme {
        Surface {
            TextSettingButton{

            }
        }
    }
}