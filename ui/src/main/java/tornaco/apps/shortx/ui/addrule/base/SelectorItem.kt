@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)

package tornaco.apps.shortx.ui.addrule.base

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeLarge
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.TipDialog
import tornaco.apps.shortx.ui.base.rememberTipDialogState
import kotlin.reflect.KClass

data class SelectorItem<out T : Any>(
    val type: KClass<out T>,
    val labelAndDescription: Pair<String, String?>,
    val icon: String,
    val iconColor: Color,
    val data: Any? = null
)

@Composable
fun <T : Any> SelectorList(
    items: List<SelectorItem<T>>,
    itemSelected: (KClass<out T>) -> Unit
) {
    SelectorList(actionItems = items, itemSelected = { kClass, data ->
        itemSelected(kClass)
    })
}

@Composable
fun <T : Any> SelectorList(
    actionItems: List<SelectorItem<T>>,
    itemSelected: (KClass<out T>, data: Any?) -> Unit
) {
    SelectorList(
        modifier = Modifier,
        childModifier = Modifier,
        actionItems = actionItems,
        itemSelected = itemSelected
    )
}

@Composable
fun <T : Any> SelectorList(
    modifier: Modifier = Modifier,
    childModifier: Modifier = Modifier,
    actionItems: List<SelectorItem<T>>,
    itemSelected: (KClass<out T>, data: Any?) -> Unit
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        actionItems.forEach {
            SelectorListItem(
                modifier = childModifier,
                icon = it.icon,
                iconColor = it.iconColor,
                title = it.labelAndDescription.first,
                text1 = it.labelAndDescription.second
            ) {
                itemSelected(it.type, it.data)
            }
        }
    }
}

@Composable
private fun SelectorListItem(
    modifier: Modifier = Modifier,
    icon: String,
    iconColor: Color,
    title: String,
    text1: String? = null,
    onClick: () -> Unit,
) {
    val tipDialogState = rememberTipDialogState(title = title, tip = text1 ?: title)
    TipDialog(state = tipDialogState)

    ShortXCard(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        shape = ShortXCardRoundedCornerShapeLarge
    ) {
        Box(modifier = modifier
            .clip(ShortXCardRoundedCornerShapeLarge)
            .combinedClickable(onLongClick = {
                tipDialogState.show()
            }) {
                onClick()
            },
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColoredIcon(
                    size = 24.dp,
                    padding = 0.dp,
                    color = iconColor.copy(alpha = ActionColorBgAlpha)
                ) {
                    RemixIcon(
                        modifier = Modifier.size(10.dp),
                        remixName = icon,
                        tint = iconColor
                    )
                }
                TinySpacer()
                Text(
                    text = title,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                TinySpacer()
            }
        }
    }
}
