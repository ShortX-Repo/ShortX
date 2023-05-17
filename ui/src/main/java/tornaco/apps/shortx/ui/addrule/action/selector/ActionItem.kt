@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import tornaco.apps.shortx.ui.addrule.action.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.action.colorForAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.TipDialog
import tornaco.apps.shortx.ui.base.rememberTipDialogState
import kotlin.reflect.KClass

data class Item(
    val type: KClass<out Action>,
    val labelAndDescription: Pair<String, String?>,
    val icon: String,
    val iconColor: Color = colorForAction(type),
    val data: Any? = null
)

fun item(
    type: KClass<out Action>, label: String, icon: String, iconColor: Color = colorForAction(type)
) = Item(type = type, labelAndDescription = label to null, icon = icon, iconColor = iconColor)


fun item(
    type: KClass<out Action>,
    label: String,
    description: String,
    icon: String,
    iconColor: Color = colorForAction(type)
) = Item(
    type = type,
    labelAndDescription = label to description,
    icon = icon,
    iconColor = iconColor
)

@Composable
fun ActionList(actionItems: List<Item>, itemSelected: (KClass<out Action>) -> Unit) {
    ActionList(actionItems = actionItems, itemSelected = { kClass, data ->
        itemSelected(kClass)
    })
}

@Composable
fun ActionList(
    actionItems: List<Item>,
    itemSelected: (KClass<out Action>, data: Any?) -> Unit
) {
    ActionList(
        modifier = Modifier, childModifier = Modifier,
        actionItems = actionItems,
        itemSelected = itemSelected
    )
}

@Composable
fun ActionList(
    modifier: Modifier = Modifier,
    childModifier: Modifier = Modifier,
    actionItems: List<Item>,
    itemSelected: (KClass<out Action>, data: Any?) -> Unit
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        actionItems.forEach {
            ActionItem(
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
fun ActionItem(
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
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        Box(modifier = modifier
            .padding(horizontal = 6.dp)
            .clip(ShortXCardRoundedCornerShape)
            .combinedClickable(onLongClick = {
                tipDialogState.show()
            }) {
                onClick()
            },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ColoredIcon(
                    size = 32.dp,
                    padding = 0.dp,
                    color = iconColor.copy(alpha = ActionColorBgAlpha)
                ) {
                    RemixIcon(
                        modifier = Modifier.size(16.dp),
                        remixName = icon,
                        tint = iconColor
                    )
                }
                TinySpacer()
                Column(modifier = Modifier) {
                    Text(
                        text = title,
                        fontSize = 12.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
