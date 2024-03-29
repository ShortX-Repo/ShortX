package tornaco.apps.shortx.ui.util

import android.content.Context
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.shortcut.ShortcutStubInfo
import java.io.File

object StubApkGen {
    private val LOGGER = Logger("ShortcutHelper")
    private val STUB_APK_TEMPLATE_PATH = "apk/shortcut-stub.apk"
    private val OUT_APK_PATH = "shortcut_stub_apks"

    fun cleanUp(context: Context) {
        try {
            File(
                context.filesDir.absolutePath
                        + File.separator
                        + OUT_APK_PATH
            ).deleteRecursively()
            LOGGER.i("cleanUp complete.")
        } catch (ignored: Throwable) {
        }
    }

    fun patchApk(context: Context, info: ShortcutStubInfo): File {
        try {
            val apkFile = File(
                File(
                    context.filesDir.absolutePath
                            + File.separator
                            + OUT_APK_PATH
                ), "stub_${info.appPkgName}.apk"
            )
            apkFile.parentFile?.mkdirs()
            context.assets.open(STUB_APK_TEMPLATE_PATH).use {
                it.copyTo(apkFile.outputStream())
            }
            return Repackager(context).patch(apkFile, info)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to patchApk", e)
        }
    }
}