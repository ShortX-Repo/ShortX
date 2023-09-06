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
fun getLocalRuleItemHandler(vm: BaseRuleListViewModel): RuleItemHandler {
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
            AddRuleActivity.edit(context, rule.id)
        }

        override fun click(rule: RuleUM) {
            edit(rule)
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
    }
    return handler
}