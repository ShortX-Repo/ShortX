package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.mapper.colorForCondition
import tornaco.apps.shortx.ui.addrule.condition.mapper.iconForCondition
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun UIConditions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    selectedFilter: ConditionCategory,
    setCondition: (Condition) -> Unit
) {
    val i18N = LocalI18N.current
    val items = listOf(
        Condition.HasNodeOnScreen::class,
        Condition.TextFromScreenNodeMatches::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelForConditionSelector(i18N, it)[0] to null,
            icon = iconForCondition(it),
            isForSubscribedOnly = !subscribeState.isSubscribed && isForSubscribedOnly(it),
            iconColor = colorForCondition(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    val isVisible = selectedFilter == ConditionCategory.All || selectedFilter == ConditionCategory.Ui
    if (!isVisible) return

    if (items.isNotEmpty()) {
        ConditionCategoryTitle(ConditionCategory.Ui)
    }

    val selectHandler: (ConditionSelectSource) -> Boolean = uiConditionSelectHandler {
        setCondition(it)
    }

    SelectorList(
        items = items,
        itemSelected = { it ->
            selectHandler(ConditionSelectSource.FromClass(it))
        }
    )
}