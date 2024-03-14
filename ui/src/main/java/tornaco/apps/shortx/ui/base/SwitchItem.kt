package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun SwitchItem(
    title: String,
    summary: String,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .clip(ShortXCardRoundedCornerShape)
            .clickable {
                onCheckChange(!isChecked)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            SmallTitle(text = title)
            SmallSpacer()
            TipBody(text = summary)
        }
        ShortXSwitch(
            checked = isChecked,
            onCheckedChange = onCheckChange,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

}