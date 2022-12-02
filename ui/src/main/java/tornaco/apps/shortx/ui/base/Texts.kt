package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LargeTitle(text: String) {
    Text(
        modifier = Modifier.padding(vertical = 12.dp),
        text = text,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun MediumTitle(text: String) {
    Text(
        modifier = Modifier.padding(vertical = 12.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun DialogTitle(text: String) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = text,
        style = MaterialTheme.typography.titleLarge
    )
}