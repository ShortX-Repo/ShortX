package tornaco.apps.shortx.ui.shortcut

import android.content.Context
import android.content.Intent
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.util.StubApkGen
import java.io.File

sealed interface ApkInstallState {
    data object Installing : ApkInstallState
    data object InstallSuccess : ApkInstallState
    data class InstallFailure(val message: String, val stackTrace: String) : ApkInstallState
}

object ShortcutStubInstaller {
    private val logger = Logger("Installer")

    suspend fun doInstallShortcutApk(
        context: Context,
        info: ShortcutStubInfo,
        effect: (ApkInstallState) -> Unit,
    ) = withContext(Dispatchers.IO) {
        logger.d("doInstallShortcutApk: $info")
        effect(ApkInstallState.Installing)

        runCatching {
            // Patch
            val apkFile = StubApkGen.patchApk(context, info)
            logger.d("apkFile: $apkFile")

            // Install.
            val pfd = ParcelFileDescriptor.open(apkFile, ParcelFileDescriptor.MODE_READ_WRITE)
            shortXManager.installApk(
                info.appPkgName,
                pfd,
                onSuccess = object : ICallback.Stub() {
                    override fun receive(data: ByteArrayWrapper?) {
                        effect(ApkInstallState.InstallSuccess)
                    }
                },
                onFailure = object : ICallback.Stub() {
                    override fun receive(data: ByteArrayWrapper?) {
                        // Fallback to install it with Intent.
                        runCatching {
                            logger.e("Install apk fail. try launch Intent")
                            viewApkFile(context, apkFile)
                            effect(ApkInstallState.InstallSuccess)
                        }.onFailure {
                            logger.e(it, "share apk as Intent err")
                            effect(
                                ApkInstallState.InstallFailure(
                                    "Apk path: ${apkFile.absolutePath}",
                                    it.stackTraceToString()
                                )
                            )
                        }
                    }
                })

        }.onFailure {
            logger.e(it, "doInstallShortcutApk err")
            effect(
                ApkInstallState.InstallFailure(
                    it.message ?: "Unknown error",
                    it.stackTraceToString()
                )
            )
        }
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