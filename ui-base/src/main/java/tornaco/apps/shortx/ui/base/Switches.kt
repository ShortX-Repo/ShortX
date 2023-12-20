package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.fallbackOnEmpty

@Composable
fun SwitchRow(
    modifier: Modifier = Modifier,
    text: String,
    tip: String? = null,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    val tipDialogState = rememberTipDialogState(
        title = text,
        tip = tip.fallbackOnEmpty(text)
    )
    tip?.let {
        TipDialog(state = tipDialogState)
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmallTitle(text = text)
            tip?.let {
                IconButton(onClick = {
                    tipDialogState.show()
                }) {
                    RemixIcon(remixName = Remix.System.information_line)
                }
            }
        }
        ShortXSwitch(checked = isChecked, onCheckedChange = onCheckChange)
    }
}

@Composable
fun SwitchColumn(
    modifier: Modifier = Modifier,
    text: String,
    tip: String? = null,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    val tipDialogState = rememberTipDialogState(
        title = text,
        tip = tip.fallbackOnEmpty(text)
    )
    tip?.let {
        TipDialog(state = tipDialogState)
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmallTitle(text = text)
            tip?.let {
                ColoredIcon(size = 24.dp, onClick = {
                    tipDialogState.show()
                }) {
                    RemixIcon(remixName = Remix.System.information_line)
                }
            }
        }
        ShortXSwitch(checked = isChecked, onCheckedChange = onCheckChange)
    }
}