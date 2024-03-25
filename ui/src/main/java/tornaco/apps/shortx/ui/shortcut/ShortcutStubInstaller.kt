package tornaco.apps.shortx.ui.shortcut

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import tornaco.apps.shortx.core.util.Logger
import java.io.File

sealed interface ApkInstallState {
    data object Installing : ApkInstallState
    data object InstallSuccess : ApkInstallState
    data class InstallFailure(val message: String) : ApkInstallState
}

object ShortcutStubInstaller {
    private val logger = Logger("Installer")

    fun doInstallShortcutApk(
        context: Context,
        info: ShortcutStubInfo,
        effect: (ApkInstallState) -> Unit,
    ) {
        logger.d("doInstallShortcutApk: $info")
        effect(ApkInstallState.Installing)
        effect(ApkInstallState.InstallFailure("Unsupported."))
    }

    private fun viewApkFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "tornaco.apps.shortx", file)
        val shareIntent = Intent(Intent.ACTION_VIEW)
        shareIntent.setDataAndType(uri, "application/vnd.android.package-archive")
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(Intent.createChooser(shareIntent, "ShortX"))
    }
}