@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxSize(),
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
    ) {

        items(actionItems) {
            ActionItem(
                modifier = childModifier,
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
    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        backgroundColor = ColorDefaults.backgroundSurfaceColor(),
        shape = ShortXCardRoundedCornerShape,
        elevation = 0.dp
    ) {
        Column(modifier = modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .clickable {
                onClick()
            }) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColoredIcon(size = 32.dp, padding = 0.dp) {
                    RemixIcon(
                        modifier = Modifier.size(16.dp),
                        remixName = icon,
                    )
                }

                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
                    Text(
                        text = title, style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp
                        )
                    )
                    text1?.let {
                        TinySpacer()
                        Text(
                            text = text1,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            maxLines = 8
                        )
                    }
                }
            }
        }
    }
}
