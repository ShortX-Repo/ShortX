package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListItem(
    titleLines: List<String>,
    icon: @Composable () -> Unit = {},
    action1: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier
            .heightIn(min = 60.dp)
            .weight(1f, fill = false)
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier) {
                Text(text = titleLines[0], style = MaterialTheme.typography.titleMedium)
                if (titleLines.size > 1) {
                    titleLines.subList(1, titleLines.size).forEach {
                        Text(text = it, style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        }

        action1()
    }
}

@Composable
fun ListItem(
    title: String,
    icon: @Composable () -> Unit = {},
    action1: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier
            .heightIn(min = 60.dp)
            .weight(1f, fill = false)
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }
        }

        action1()
    }
}

@Composable
fun ListItem(
    title: String? = null,
    text1: String? = null,
    text2: String? = null,
    checked: Boolean,
    onClick: () -> Unit,
    onCheckedChange: ((Boolean) -> Unit),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(end = 24.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.Commit, contentDescription = "Icon")
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier) {
                title?.let {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W600
                        )
                    )
                }
                text1?.let {
                    Text(
                        text = text1,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                }
                text2?.let {
                    Text(
                        text = text2,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            IconButton(
                modifier = Modifier,
                onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More")
            }
            Switch(
                modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
@Preview
fun ListItemPreview() {
    LazyColumn {
        items(10) {
            ListItem(title = "Title",
                text1 = "Text1",
                text2 = "Text2",
                checked = true,
                onClick = {},
                onCheckedChange = {})
        }
    }
}