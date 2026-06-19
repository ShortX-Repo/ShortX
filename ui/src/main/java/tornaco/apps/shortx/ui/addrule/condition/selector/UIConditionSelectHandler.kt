package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.getConditionAs
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.HasFoundNodeOnScreenDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberHasFoundNodeOnScreenState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.core.i18n.I18N_R

@Composable
fun uiConditionSelectHandler(addCondition: (Condition) -> Unit): (ConditionSelectSource) -> Boolean {
    val hasNodeOnScreenState = rememberHasFoundNodeOnScreenState(
        selectorTitle = LocalI18N.current[I18N_R.UI_CONDITION_IS_HAS_NODE_ON_SCREEN],
        canSelectPkgName = false,
        canSelectActivity = false,
        canSelectTimeout = false,
        onSelected = {
            addCondition(
                Condition.HasNodeOnScreen(
                    packageName = it.packageName,
                    componentName = it.componentName,
                    detectTimeout = it.detectTimeout,
                    matchers = it.matchers
                )
            )
        })
    HasFoundNodeOnScreenDialog(state = hasNodeOnScreenState)

    val textFromScreenNodeMatchesState = rememberTextFromScreenNodeMatchesState {
        addCondition(it)
    }
    TextFromScreenNodeMatchesSelector(dialogState = textFromScreenNodeMatchesState)

    return { source ->
        when (source.clazz) {
            Condition.HasNodeOnScreen::class -> {
                hasNodeOnScreenState.show(source.getConditionAs<Condition.HasNodeOnScreen>()?.let {
                    Fact.HasFoundNodeOnScreen(
                        packageName = it.packageName,
                        componentName = it.componentName,
                        detectTimeout = it.detectTimeout,
                        matchers = it.matchers
                    )
                })
                true
            }

            Condition.TextFromScreenNodeMatches::class -> {
                textFromScreenNodeMatchesState.show(source.getConditionAs())
                true
            }

            else -> false
        }
    }
}