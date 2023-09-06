package tornaco.apps.shortx.ui.main.da

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.ShortXBottomSheetState
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.util.Permissions.shouldCheckStoragePermissionBeforeWriteDownloads

@Composable
fun getLocalDASetItemHandler(
    bottomSheetState: ShortXBottomSheetState,
    vm: DAListViewModel
): DASetItemHandler {
    val shareDialogState = rememberShareDialogState("share.shortx")
    ShareDialog(state = shareDialogState)

    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {})

    val context = LocalContext.current
    val props = LocalShortXProps.current

    val handler = object : DASetItemHandler {
        override fun dismiss(da: DirectActionUM) {
            vm.dismissDASet(da, props.firstOrNull())
        }

        override fun rename(da: DirectActionUM, newName: String) {
            vm.rename(da, newName, props.firstOrNull())
        }

        override fun click(da: DirectActionUM) {
            bottomSheetState.show(da.id)
        }

        override fun share(da: DirectActionUM) {
            if (context.shouldCheckStoragePermissionBeforeWriteDownloads()) {
                permissionRequest.launch(
                    arrayOf(
                        ManifestCompat.permission.READ_EXTERNAL_STORAGE,
                        ManifestCompat.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )
            } else {
                vm.daSetToShareString(da)?.let {
                    shareDialogState.show(da.title, it)
                }
            }
        }
    }

    return handler
}