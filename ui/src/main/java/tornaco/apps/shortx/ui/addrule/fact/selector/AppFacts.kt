package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.fact.FactSelectSource
import tornaco.apps.shortx.ui.addrule.fact.factSelectHandler
import tornaco.apps.shortx.ui.addrule.fact.mapper.colorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.allFactClasses
import tornaco.apps.shortx.ui.base.LocalI18N

@Composable
fun AppFacts(setFact: (Fact) -> Unit) {
    val i18N = LocalI18N.current
    val items = allFactClasses.map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForFactSelector(i18N, it),
            icon = imageVectorForFact(it),
            iconColor = colorForFact(it)
        )
    }

    val itemSelected = factSelectHandler(setFact)

    SelectorList(
        items = items,
        itemSelected = { it ->
            itemSelected(FactSelectSource.FromClass(it))
        }
    )
}