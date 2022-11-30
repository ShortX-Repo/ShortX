package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ListItem(
    title: String,
    onClick: () -> Unit,
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick()
        }
        .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Filled.Commit, contentDescription = "Icon")
        Spacer(modifier = Modifier.size(16.dp))
        Column(modifier = Modifier) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun ListItem(
    title: String,
    text1: String,
    text2: String,
    checked: Boolean,
    onClick: () -> Unit,
    onCheckedChange: ((Boolean) -> Unit),
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick()
        }
        .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {

        Row(modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Commit, contentDescription = "Icon")
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = text1, style = MaterialTheme.typography.bodyMedium)
                Text(text = text2, style = MaterialTheme.typography.bodySmall)
            }
        }

        Switch(checked = checked, onCheckedChange = onCheckedChange)
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