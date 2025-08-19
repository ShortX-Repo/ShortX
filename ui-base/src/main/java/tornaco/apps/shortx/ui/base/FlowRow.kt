package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> CenterFlowRow(
    items: List<T>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    itemSize: Dp = 64.dp,
    itemContent: @Composable (item: T) -> Unit
) {
    BoxWithConstraints(modifier) {
        val maxWidth = maxWidth
        val itemCountEachRow = (maxWidth / itemSize).toInt()
        val spaceLeft = maxWidth - (itemCountEachRow * itemSize)
        val paddingStart = spaceLeft / 2
        FlowRow(
            modifier = Modifier.padding(start = paddingStart),
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = verticalArrangement,
            maxItemsInEachRow = maxItemsInEachRow
        ) {
            items.forEach {
                Box(modifier = Modifier.size(itemSize), contentAlignment = Alignment.Center) {
                    itemContent(it)
                }
            }
        }
    }
}