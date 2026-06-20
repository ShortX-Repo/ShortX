package tornaco.apps.shortx.ui.main.rule

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.ui.addrule.AddRuleActivity
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.util.Permissions.shouldCheckStoragePermissionBeforeWriteDownloads

@Composable
fun getLocalRuleItemHandler(
    vm: BaseRuleListViewModel,
    onSelect: (RuleUM) -> Unit = {}
): RuleItemHandler {
    val context = LocalContext.current
    val shareDialogState = rememberShareDialogState("share.shortx")
    ShareDialog(state = shareDialogState)

    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {})

    val props = LocalShortXProps.current

    val handler = object : RuleItemHandler {
        override fun run(rule: RuleUM) {
            vm.directExecuteRuleActions(rule)
        }

        override fun dup(rule: RuleUM) {
            vm.dupRule(rule.id, props.firstOrNull())
        }

        override fun delete(rule: RuleUM) {
            vm.deleteRule(rule.id)
        }

        override fun edit(rule: RuleUM) {
            AddRuleActivity.edit(context, rule.id, readOnly = rule.isLocked)
        }

        override fun click(rule: RuleUM) {
            edit(rule)
        }

        override fun select(rule: RuleUM) {
            onSelect(rule)
        }

        override fun share(rule: RuleUM) {
            if (context.shouldCheckStoragePermissionBeforeWriteDownloads()) {
                permissionRequest.launch(
                    arrayOf(
                        ManifestCompat.permission.READ_EXTERNAL_STORAGE,
                        ManifestCompat.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )
            } else {
                vm.ruleToShareString(rule)?.let {
                    shareDialogState.show(rule.title, it)
                }
            }
        }

        override fun share(rules: List<RuleUM>) {
            if (rules.isEmpty()) return
            if (context.shouldCheckStoragePermissionBeforeWriteDownloads()) {
                permissionRequest.launch(
                    arrayOf(
                        ManifestCompat.permission.READ_EXTERNAL_STORAGE,
                        ManifestCompat.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )
            } else {
                val title = rules.joinToString(separator = ", ", limit = 3) { it.title }
                vm.rulesToShareString(title, rules)?.let {
                    shareDialogState.show(title, it)
                }
            }
        }

        override fun moveToSet(rule: RuleUM, setId: String) {
            vm.moveToSet(rule, setId, props.firstOrNull())
        }

        override fun moveOutSet(rule: RuleUM) {
            vm.moveOutSet(rule.rule, props.firstOrNull())
        }

        override fun stopAllJobs(rule: RuleUM) {
            vm.stopJobs(rule)
        }

        override fun setRuleEnabled(rule: RuleUM, enable: Boolean) {
            vm.setRuleEnabled(rule.id, enable)
        }

        override fun extractAsDA(title: String, rule: RuleUM, token: String?) {
            vm.extractAsDA(title, rule, token)
        }

        override fun togglePin(rule: RuleUM) {
            vm.togglePin(rule)
        }

        override fun toggleLock(rule: RuleUM) {
            vm.toggleLock(rule)
        }
    }
    return handler
}
