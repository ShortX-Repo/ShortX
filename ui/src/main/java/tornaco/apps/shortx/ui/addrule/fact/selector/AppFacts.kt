package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.fact.FactSelectSource
import tornaco.apps.shortx.ui.addrule.fact.appFactSelectHandler
import tornaco.apps.shortx.ui.addrule.fact.mapper.colorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun AppFacts(keyword: String, subscribeState: LVLStateHolder.State, setFact: (Fact) -> Unit) {
    val i18N = LocalI18N.current
    val items = listOf(
        Fact.AnyActivityStarted::class,
        Fact.ActivityStarted::class,

        Fact.AppBecomeBackground::class,
        Fact.AppBecomeForeground::class,

        Fact.AppProcessStarted::class,

        Fact.NotificationPosted::class,
        Fact.NotificationRemoved::class,
        Fact.NotificationUpdated::class,

        Fact.TaskRemoved::class,
        Fact.TaskRemovedAny::class,

        Fact.AppAdded::class,
        Fact.AppRemoved::class,
        Fact.AppUpdated::class,

        Fact.PkgStopRunning::class,
        Fact.PkgStopRunningAny::class,

        Fact.AudioFocusGain::class,
        Fact.AudioFocusLost::class,
        Fact.AudioFocusChanged::class,

        Fact.HasFoundNodeOnScreen::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForFactSelector(i18N, it),
            icon = imageVectorForFact(it),
            isForSubscribedOnly = remember(subscribeState) {
                !subscribeState.isSubscribed && isForSubscribedOnly(it)
            },
            iconColor = colorForFact(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    if (items.isNotEmpty()) {
        FactCategoryTitle(FactCategory.Apps)
    }

    val itemSelected = appFactSelectHandler(setFact)

    SelectorList(
        items = items,
        itemSelected = { it ->
            itemSelected(FactSelectSource.FromClass(it))
        }
    )
}