package tornaco.apps.shortx.ui.main.rule

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.base.ShortXBottomSheetState
import tornaco.apps.shortx.ui.main.model.RuleUM

@Composable
fun getLocalRuleSetItemHandler(
    vm: RuleListViewModel,
    bottomSheetState: ShortXBottomSheetState
): RuleSetItemHandler {
    val handler = object : RuleSetItemHandler {
        override fun delete(rule: RuleUM) {

        }

        override fun edit(rule: RuleUM) {

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