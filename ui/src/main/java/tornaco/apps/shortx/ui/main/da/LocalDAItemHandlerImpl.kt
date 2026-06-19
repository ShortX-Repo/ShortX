package tornaco.apps.shortx.ui.main.da

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.ui.addda.AddDirectActionActivity
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.util.ClipboardUtils
import tornaco.apps.shortx.ui.util.Permissions.shouldCheckStoragePermissionBeforeWriteDownloads
import tornaco.apps.shortx.core.i18n.I18N_R

@Composable
fun getLocalDAItemHandler(
    vm: BaseDAListViewModel,
    onSelect: (DirectActionUM) -> Unit = {}
): DAItemHandler {
    val context = LocalContext.current
    val i18N = LocalI18N.current

    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {})

    val shareDialogState = rememberShareDialogState("share.shortx")
    ShareDialog(state = shareDialogState)

    val props = LocalShortXProps.current

    val daHandler = object : DAItemHandler {
        override fun run(da: DirectActionUM) {
            vm.executeDA(da)
        }

        override fun dup(da: DirectActionUM) {
            vm.dupDA(da.id, props.firstOrNull())
        }

        override fun delete(da: DirectActionUM) {
            vm.deleteDA(da.id)
        }

        override fun edit(da: DirectActionUM) {
            AddDirectActionActivity.edit(context, da.id)
        }

        override fun click(da: DirectActionUM) {
            edit(da)
        }

        override fun select(da: DirectActionUM) {
            onSelect(da)
        }

        override fun createApk(da: DirectActionUM) {
            vm.createApk(context, da)
        }

        override fun deepLink(da: DirectActionUM) {
            val uri = "shortx://run?id=${da.id}"
            ClipboardUtils.copyToClipboard(context, "uri", uri)
            Toast.makeText(
                context,
                i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                Toast.LENGTH_LONG
            ).show()
        }

        override fun shellCommand(da: DirectActionUM) {
            val cmd = "am shortx run ${da.id}"
            ClipboardUtils.copyToClipboard(context, "cmd", cmd)
            Toast.makeText(
                context,
                i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                Toast.LENGTH_LONG
            ).show()
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
                vm.daToShareString(da)?.let {
                    shareDialogState.show(da.title, it)
                }
            }
        }

        override fun share(das: List<DirectActionUM>) {
            if (das.isEmpty()) return
            if (context.shouldCheckStoragePermissionBeforeWriteDownloads()) {
                permissionRequest.launch(
                    arrayOf(
                        ManifestCompat.permission.READ_EXTERNAL_STORAGE,
                        ManifestCompat.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )
            } else {
                val title = das.joinToString(separator = ", ", limit = 3) { it.title }
                vm.dasToShareString(title, das)?.let {
                    shareDialogState.show(title, it)
                }
            }
        }

        override fun moveToSet(da: DirectActionUM, setId: String) {
            vm.moveToSet(da, setId, props.firstOrNull())
        }

        override fun moveOutSet(da: DirectActionUM) {
            vm.moveOutSet(da.directAction, props.firstOrNull())
        }

        override fun stopAllJobs(da: DirectActionUM) {
            vm.stopJobs(da)
        }
        
        override fun togglePin(da: DirectActionUM) {
            vm.togglePin(da)
        }
    }

    return daHandler
}
