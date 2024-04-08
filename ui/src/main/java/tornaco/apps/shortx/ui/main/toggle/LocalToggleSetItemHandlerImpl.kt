package tornaco.apps.shortx.ui.main.toggle

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.ShortXBottomSheetState
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.main.model.ToggleUM
import tornaco.apps.shortx.ui.util.Permissions.shouldCheckStoragePermissionBeforeWriteDownloads

@Composable
fun getLocalToggleSetItemHandler(
    vm: ToggleListViewModel,
    bottomSheetState: ShortXBottomSheetState
): ToggleSetItemHandler {

    val shareDialogState = rememberShareDialogState("share.shortx")
    ShareDialog(state = shareDialogState)

    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {})

    val context = LocalContext.current
    val props = LocalShortXProps.current

    val handler = object : ToggleSetItemHandler {
        override fun dismiss(toggle: ToggleUM) {
        }

        override fun delete(toggle: ToggleUM, alsoDeleteItems: Boolean) {
        }

        override fun rename(toggle: ToggleUM, newName: String) {
        }

        override fun click(toggle: ToggleUM) {
            bottomSheetState.show(toggle.id)
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
                vm.toggleSetToShareString(toggle)?.let {
                    shareDialogState.show(toggle.title, it)
                }
            }
        }

        override fun setToggleEnabled(toggle: ToggleUM, enable: Boolean) {
        }
    }
    return handler
}