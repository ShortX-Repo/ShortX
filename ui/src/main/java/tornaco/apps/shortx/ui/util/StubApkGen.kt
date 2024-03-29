package tornaco.apps.shortx.ui.util

import android.content.Context
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.shortcut.ShortcutStubInfo
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

object StubApkGen {
    private val LOGGER = Logger("ShortcutHelper")
    private const val STUB_APK_TEMPLATE_PATH = "apk/shortcut-stub.apk"
    private const val OUT_APK_PATH = "shortcut_stub_apks"
    private const val KEYSTORE_DOWNLOAD_PATH = "download.keystore"
    private const val APK_DOWNLOAD_PATH = "download.apk"

    private val logger = Logger("StubApkGen")

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
        val keystoreDownloadFile = File(context.filesDir.absolutePath, KEYSTORE_DOWNLOAD_PATH)
        val apkTemplateDownloadFile = File(context.filesDir.absolutePath, APK_DOWNLOAD_PATH)

        try {
            if (!keystoreDownloadFile.exists()) {
                keystoreDownloadFile.parentFile?.mkdirs()
                downloadFile(BuildProp.SHORTCUT_STUB_KEYSTORE_URL, keystoreDownloadFile)
                logger.d("Download keystore success.")
            } else {
                logger.d("keystore exists.")
            }

            if (!apkTemplateDownloadFile.exists()) {
                apkTemplateDownloadFile.parentFile?.mkdirs()
                downloadFile(BuildProp.SHORTCUT_STUB_APK_URL, apkTemplateDownloadFile)
                logger.d("Download apk success.")
            } else {
                logger.d("apk exists.")
            }

            val shortcutApkFile = File(
                File(
                    context.filesDir.absolutePath
                            + File.separator
                            + OUT_APK_PATH
                ), "stub_${info.appPkgName}.apk"
            )
            shortcutApkFile.parentFile?.mkdirs()
            if (shortcutApkFile.exists()) shortcutApkFile.delete()
            apkTemplateDownloadFile.copyTo(shortcutApkFile)


            return Repackager(context).patch(shortcutApkFile, keystoreDownloadFile, info)
        } catch (e: Exception) {
            keystoreDownloadFile.delete()
            apkTemplateDownloadFile.delete()
            throw IllegalStateException("Failed to patchApk - ${e.message}", e)
        }
    }

    private fun downloadFile(fileUrl: String, outputFile: File) {
        try {
            val url = URL(fileUrl)
            val inputStream: InputStream = BufferedInputStream(url.openStream())
            val outputStream: OutputStream = FileOutputStream(outputFile)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            throw IOException("Fail to download file: $fileUrl", e)
        }
    }
}