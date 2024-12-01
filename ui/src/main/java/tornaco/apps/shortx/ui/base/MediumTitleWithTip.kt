package tornaco.apps.shortx.ui.base


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        MediumTitle(text)

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

@Composable
fun LargeTitleWithTip(text: String, tipText: String) {
    val tipDialogState = rememberTipDialogState(title = text, tip = tipText)

    Row(
        modifier = Modifier
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LargeTitle(text)

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

@Composable
fun FieldTitleWithTip(text: String, tipText: String) {
    val tipDialogState = rememberTipDialogState(title = text, tip = tipText)

    Row(
        modifier = Modifier
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FieldTitle(text = text)

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