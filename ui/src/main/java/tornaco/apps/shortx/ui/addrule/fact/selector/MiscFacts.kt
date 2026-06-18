package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.fact.FactSelectSource
import tornaco.apps.shortx.ui.addrule.fact.mapper.colorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.miscFactSelectHandler
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun MiscFacts(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    selectedFilter: FactCategory,
    setFact: (Fact) -> Unit
) {
    val i18N = LocalI18N.current
    val items = mutableListOf(
        Fact.OnQSTileClick::class,
        Fact.DeepLinkCall::class,
        Fact.IncomingShare::class,
        Fact.BrowserIntercept::class,
        Fact.GlobalVarChanged::class,
        Fact.BackNavStart::class,
        Fact.BackNavDone::class,
        Fact.MethodHook::class,
        Fact.OnMenuActionTrigger::class,
    ).apply {
        if (BuildPropInternal.APP_IS_DEBUG) {
            Fact.OnStartDynamicShortcut::class
        }
        add(Fact.AnyFact::class)
    }.map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForFactSelector(i18N, it),
            icon = imageVectorForFact(it),
            isForSubscribedOnly = !subscribeState.isSubscribed && isForSubscribedOnly(it),
            iconColor = colorForFact(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    val isVisible = selectedFilter == FactCategory.All || selectedFilter == FactCategory.Advanced
    if (!isVisible) return

    if (items.isNotEmpty()) {
        FactCategoryTitle(FactCategory.Advanced)
    }

    val itemSelected = miscFactSelectHandler(setFact)

    SelectorList(
        items = items,
        itemSelected = {
            itemSelected(FactSelectSource.FromClass(it))
        }
    )
}