package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.W600,
            fontSize = 18.sp
        )
    )
}

@Composable
fun SmallTitle(text: String) {
    Text(
        modifier = Modifier.padding(vertical = 12.dp),
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.W500,
            fontSize = 14.sp
        )
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