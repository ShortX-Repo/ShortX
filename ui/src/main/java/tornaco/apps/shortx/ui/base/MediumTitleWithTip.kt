package tornaco.apps.shortx.ui.base


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix

@Composable
fun MediumTitleWithTip(text: String, tipText: String) {
    val tipDialogState = rememberTipDialogState(title = text, tip = tipText)

    Row(
        modifier = Modifier
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.W500,
                fontSize = 17.sp
            )
        )

        IconButton(onClick = {
            tipDialogState.show()
        }) {
            RemixIcon(
                remixName = Remix.System.question_line,
                modifier = Modifier.size(16.dp)
            )
        }
    }

    TipDialog(tipDialogState)
}