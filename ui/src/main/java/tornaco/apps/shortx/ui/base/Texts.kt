package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
            fontWeight = FontWeight.W600,
            fontSize = 16.sp
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

@Composable
fun DialogMessage(text: String) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = text,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun TipBody(text: String) {
    Text(
        modifier = Modifier.padding(vertical = 12.dp),
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 10.sp
        )
    )
}

@Composable
fun TipRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(modifier = Modifier.size(8.dp), imageVector = Icons.Outlined.Lightbulb)
        Text(
            modifier = Modifier,
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun CategoryTitle(title: String) {
    Box(
        modifier = Modifier
            .padding(top = 24.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )
        )
    }
}