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

@Composable
fun OtherConditions(setCondition: (Condition) -> Unit) {
    val i18N = LocalI18N.current
    val items = listOf(
        Condition.MVEL::class,
        Condition.TRUE::class,
        Condition.EvaluateGlobalVar::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelForConditionSelector(i18N, it)[0] to null,
            icon = iconForCondition(it),
            iconColor = colorForCondition(it)
        )
    }

    val selectHandler: (ConditionSelectSource) -> Boolean = conditionSelectHandler {
        setCondition(it)
    }

    SelectorList(
        items = items,
        itemSelected = { it ->
            selectHandler(ConditionSelectSource.FromClass(it))
        }
    )
}