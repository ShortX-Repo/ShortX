package tornaco.apps.shortx.ui.util

import android.content.Context
import tornaco.apps.shortx.core.util.Logger
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

    fun patchApk(context: Context): File {
        try {
            val apkFile = File(
                File(
                    context.filesDir.absolutePath
                            + File.separator
                            + OUT_APK_PATH
                ), "stub.apk"
            )
            require(apkFile.parentFile?.mkdirs() ?: false) {
                "Fail mk dir."
            }
            context.assets.open(STUB_APK_TEMPLATE_PATH).use {
                it.copyTo(apkFile.outputStream())
            }
            return Repackager(context).patch(apkFile)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to patchApk", e)
        }
    }
}