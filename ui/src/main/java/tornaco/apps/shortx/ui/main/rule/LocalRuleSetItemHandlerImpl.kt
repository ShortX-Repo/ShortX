package tornaco.apps.shortx.ui.main.rule

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.ShortXBottomSheetState
import tornaco.apps.shortx.ui.main.model.RuleUM

@Composable
fun getLocalRuleSetItemHandler(
    vm: RuleListViewModel,
    bottomSheetState: ShortXBottomSheetState
): RuleSetItemHandler {
    val props = LocalShortXProps.current
    val handler = object : RuleSetItemHandler {
        override fun dismiss(rule: RuleUM) {
            vm.dismissRuleSet(rule, props.firstOrNull())
        }

        override fun rename(rule: RuleUM, newName: String) {
            vm.rename(rule, newName, props.firstOrNull())
        }

        override fun click(rule: RuleUM) {
            bottomSheetState.show(rule.id)
        }

        override fun share(rule: RuleUM) {

        }

        override fun setRuleEnabled(rule: RuleUM, enable: Boolean) {
            vm.setRuleSetEnabled(rule.id, enable)
        }
    }
    return handler
}