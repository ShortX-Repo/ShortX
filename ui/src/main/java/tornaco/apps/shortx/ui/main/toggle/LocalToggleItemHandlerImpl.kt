package tornaco.apps.shortx.ui.main.toggle

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.ui.addtoggle.AddToggleActivity
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.main.model.ToggleUM
import tornaco.apps.shortx.ui.util.Permissions.shouldCheckStoragePermissionBeforeWriteDownloads

@Composable
fun getLocalToggleItemHandler(vm: BaseToggleListViewModel): ToggleItemHandler {
    val context = LocalContext.current
    val shareDialogState = rememberShareDialogState("share.shortx")
    ShareDialog(state = shareDialogState)

    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {})

    val props = LocalShortXProps.current

    val handler = object : ToggleItemHandler {
        override fun run(toggle: ToggleUM) {
        }

        override fun dup(toggle: ToggleUM) {
        }

        override fun delete(toggle: ToggleUM) {
            vm.deleteToggle(toggle.id)
        }

        override fun edit(toggle: ToggleUM) {
            AddToggleActivity.edit(context, toggle.id)
        }

        override fun click(toggle: ToggleUM) {
            edit(toggle)
        }

        override fun share(toggle: ToggleUM) {
            if (context.shouldCheckStoragePermissionBeforeWriteDownloads()) {
                permissionRequest.launch(
                    arrayOf(
                        ManifestCompat.permission.READ_EXTERNAL_STORAGE,
                        ManifestCompat.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )
            } else {

            }
        }

        override fun moveToSet(toggle: ToggleUM, setId: String) {
        }

        override fun moveOutSet(toggle: ToggleUM) {
        }

        override fun stopAllJobs(toggle: ToggleUM) {
        }

        override fun setToggleEnabled(toggle: ToggleUM, enable: Boolean) {
        }
    }
    return handler
}