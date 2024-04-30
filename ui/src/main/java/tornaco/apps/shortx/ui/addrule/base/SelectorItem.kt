@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalLayoutApi::class,
)

package tornaco.apps.shortx.ui.addrule.base

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeL
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import kotlin.reflect.KClass

data class SelectorItem<out T : Any>(
    val type: KClass<out T>,
    val labelAndDescription: Pair<String, String?>,
    val icon: String,
    val iconColor: Color,
    val isForSubscribedOnly: Boolean,
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
    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

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
                text1 = it.labelAndDescription.second,
                isForSubscribedOnly = it.isForSubscribedOnly
            ) {
                if (it.isForSubscribedOnly) {
                    subscribeDialogState.show()
                } else {
                    itemSelected(it.type, it.data)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorListItem(
    modifier: Modifier = Modifier,
    icon: String,
    iconColor: Color,
    title: String,
    text1: String? = null,
    isForSubscribedOnly: Boolean,
    onClick: () -> Unit,
) {
    ShortXCard(
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
        shape = ShortXCardRoundedCornerShapeL
    ) {
        Box(modifier = modifier
            .clip(ShortXCardRoundedCornerShapeL)
            .combinedClickable(onLongClick = {
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

                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    if (text1 != null && text1.length < 10) {
                        Text(
                            text = text1,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                TinySpacer()

                if (isForSubscribedOnly) {
                    OutlineBadge(text = LocalI18N.current["ui.subscription.subscribed.only"])
                    TinySpacer()
                }
            }
        }
    }
}

@Composable
@Preview
private fun SelectorListItemPreview() {
    CompositionLocalProvider(LocalI18N provides object : I18N {
        override fun get(key: String, args: Map<String, String>, fallback: String?): String {
            return key.substringAfterLast(".")
        }
    }) {
        Column {
            SelectorListItem(
                icon = Remix.System.apps_line,
                iconColor = Color.Red,
                title = "Just do it",
                text1 = null,
                isForSubscribedOnly = true,
                onClick = {}
            )

            SectionSpacer()

            SelectorListItem(
                icon = Remix.System.apps_line,
                iconColor = Color.Red,
                title = "Just do it",
                text1 = "This is a good question",
                isForSubscribedOnly = false,
                onClick = {}
            )

            SectionSpacer()

            SelectorListItem(
                icon = Remix.System.apps_line,
                iconColor = Color.Red,
                title = "Just do it",
                text1 = "Question",
                isForSubscribedOnly = true,
                onClick = {}
            )
        }
    }
}