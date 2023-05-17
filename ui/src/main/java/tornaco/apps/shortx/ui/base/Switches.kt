package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SwitchRow(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SmallTitle(text = text)
        StandardSpacer()
        ShortXSwitch(checked = isChecked, onCheckedChange = onCheckChange)
    }
}