package tornaco.apps.shortx.ui.main.rule

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.ShortXBottomSheetState
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.util.Permissions.shouldCheckStoragePermissionBeforeWriteDownloads

@Composable
fun getLocalRuleSetItemHandler(
    vm: RuleListViewModel,
    bottomSheetState: ShortXBottomSheetState
): RuleSetItemHandler {

    val shareDialogState = rememberShareDialogState("share.shortx")
    ShareDialog(state = shareDialogState)

    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {})

    val context = LocalContext.current
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
            if (context.shouldCheckStoragePermissionBeforeWriteDownloads()) {
                permissionRequest.launch(
                    arrayOf(
                        ManifestCompat.permission.READ_EXTERNAL_STORAGE,
                        ManifestCompat.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )
            } else {
                vm.ruleSetToShareString(rule)?.let {
                    shareDialogState.show(rule.title, it)
                }
            }
        }

        override fun setRuleEnabled(rule: RuleUM, enable: Boolean) {
            vm.setRuleSetEnabled(rule.id, enable)
        }
    }
    return handler
}