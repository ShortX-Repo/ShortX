@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import kotlin.reflect.KClass

data class Item(
    val type: KClass<out Action>,
    val labelAndDescription: Pair<String, String?>,
    val icon: String,
    val data: Any? = null
)

fun item(
    type: KClass<out Action>, label: String, icon: String
) = Item(type = type, labelAndDescription = label to null, icon = icon)


fun item(
    type: KClass<out Action>, label: String, description: String, icon: String
) = Item(type = type, labelAndDescription = label to description, icon = icon)

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
        modifier = modifier,
    ) {
        actionItems.forEach {
            ActionItem(
                modifier = childModifier.width(88.dp),
                icon = it.icon,
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
    title: String,
    text1: String? = null,
    onClick: () -> Unit,
) {
    val tipDialogState = rememberTipDialogState(title = title, tip = text1 ?: title)
    TipDialog(state = tipDialogState)

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        backgroundColor = ColorDefaults.backgroundSurfaceColor(),
        shape = ShortXCardRoundedCornerShape,
        elevation = 0.dp
    ) {
        Box(modifier = modifier
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
                ColoredIcon(size = 32.dp, padding = 0.dp) {
                    RemixIcon(
                        modifier = Modifier.size(16.dp),
                        remixName = icon,
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
