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
fun OtherConditions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    selectedFilter: ConditionCategory,
    setCondition: (Condition) -> Unit
) {
    val i18N = LocalI18N.current
    val items = listOf(
        Condition.MVEL::class,
        Condition.JS::class,
        Condition.TRUE::class,
        Condition.RequireDelay::class,
        Condition.EvaluateGlobalVar::class,
        Condition.EvaluateContextVar::class,
        Condition.EvaluateLocalVar::class,
        Condition.IsRuleEnabled::class,
        Condition.RequireFactTag::class,
        Condition.TheXXTimeToday::class,
        Condition.HasFoundPointsByColor::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelForConditionSelector(i18N, it)[0] to null,
            icon = iconForCondition(it),
            iconColor = colorForCondition(it),
            isForSubscribedOnly = !subscribeState.isSubscribed && isForSubscribedOnly(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    val isVisible = selectedFilter == ConditionCategory.All || selectedFilter == ConditionCategory.Advanced
    if (!isVisible) return

    if (items.isNotEmpty()) {
        ConditionCategoryTitle(ConditionCategory.Advanced)
    }

    val selectHandler: (ConditionSelectSource) -> Boolean = otherConditionSelectHandler {
        setCondition(it)
    }

    SelectorList(
        items = items,
        itemSelected = { it ->
            selectHandler(ConditionSelectSource.FromClass(it))
        }
    )
}